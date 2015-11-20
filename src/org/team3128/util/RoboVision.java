package org.team3128.util;

import java.util.LinkedList;

import org.team3128.Log;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.ShapeMode;
import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.AxisCamera;




/*
 *       /^\ 
 *      / _ \
 *     / [ ] \
 *    /  [_]  \
 *   /    _    \
 *  /    (_)    \
 * /_____________\
 * -----------------------------------------------------
 * UNTESTED CODE!
 * This class has never been tried on an actual robot.
 * It may be non or partially functional.
 * Do not make any assumptions as to its behavior!
 * And don't blink.  Not even for a second.
 * -----------------------------------------------------*/
public class RoboVision
{

    Image rawImage;
    Image thresheldImage;
    Image filteredImage;
    
    NIVision.ParticleFilterCriteria2[] filterCriteria;
    
    Range hRange, sRange, vRange;
    
    AxisCamera camera;
    
    boolean debug;
            
    //How close the rectangularity score has to be for something to be considered a target
    final static int RECTANGULARITY_MATCH_LIMIT = 20;
    
    final static int MINIMUM_ASPECT_RATIO_SCORE = 70;

    /**
     * 
     * @param camera
     * @param minimumArea
     */
    public RoboVision(AxisCamera camera, double minimumArea, boolean debug)
    {
    	rawImage = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
    	thresheldImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
    	filteredImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
    	
    	this.camera = camera;
    	
    	this.debug = debug;
    	
    	filterCriteria = new NIVision.ParticleFilterCriteria2[1];
    	filterCriteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, minimumArea, Short.MAX_VALUE, 0, 0);
    }
   
    /**
     * Find a single-color, rectangle-ish object
     * @param aspectRatio the horizontal to vertical distance ratio of the (bounding box surrounding the) object
     * @param rectangularity score from 0-100 of how rectangular the object is.  See {@link ParticleReport#rectangularity} for details
     */
    public LinkedList<ParticleReport> findSingleTarget(Range hRange, Range sRange, Range vRange, double aspectRatio, double rectangularity) 
    {
        LinkedList<ParticleReport> targets = new LinkedList<ParticleReport>();

        try 
        {
            /**
             * Do the image capture with the camera and apply the algorithm
             * described above.
             */
            camera.getImage(rawImage);
            //DO NOT CHANGE THESE VALUES BELOW.
            NIVision.imaqColorThreshold(thresheldImage, rawImage, 255, ColorMode.HSV, hRange, sRange, vRange);
            
            if(debug)
            {
            	Log.debug("RoboVision", "Just finished thresholding.  Particles: " + NIVision.imaqCountParticles(thresheldImage, 1));
        	}

            //thresholdImage.write("/threshold.bmp");
            //NIVision.imaqWriteBMPFile(thresholdedImage, "/home/lvuser/RoboVision/thresholdedImage.bmp", 0, new RGBValue(255, 255, 255, 1));

            //find particles
            NIVision.imaqParticleFilter4(filteredImage, thresheldImage, filterCriteria, new NIVision.ParticleFilterOptions2(0,0,1,1), null);
            
            //iterate through each particle and score to see if it is a target
            int numParticles = NIVision.imaqCountParticles(filteredImage, 1);
                                    
            if(numParticles > 0)
            {
                for (int i = 0; i < numParticles; i++)
                {

                    ParticleReport report = new ParticleReport(filteredImage, i);

                    //Score each particle on rectangularity and aspect ratio
                    double aspectRatioScore = scoreAspectRatio(report, aspectRatio);               
                    
                    double rectangularityDifference = Math.abs(report.rectangularity - rectangularity);
                    
                    //we want particles with a close aspect ratio and rectangularity to what we want, as well as a large area
                    report.setScore(aspectRatioScore * RobotMath.clampDouble(RECTANGULARITY_MATCH_LIMIT - rectangularityDifference, 0, RECTANGULARITY_MATCH_LIMIT) * report.area);
                    
                    NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);

                    if(aspectRatioScore > MINIMUM_ASPECT_RATIO_SCORE && report.rectangularity >= rectangularity)
                    {
                    	if(debug)
                        {
	                        System.out.println("particle: " + i + "is a Horizontal Target centerX: " + report.center_of_mass_x + "centerY: " + report.center_of_mass_y);
	                        NIVision.imaqDrawShapeOnImage(rawImage, rawImage, rect, DrawMode.PAINT_INVERT, ShapeMode.SHAPE_RECT, 0.0f);
                        }
                    	
                    	targets.add(report);
                    	
                    } 
                    else
                    {
                    	if(debug)
                        {
	                        System.out.println("particle: " + i + "is not a Target centerX: " + report.center_of_mass_x + "centerY: " + report.center_of_mass_x);
	                        NIVision.imaqDrawShapeOnImage(rawImage, rawImage, rect, DrawMode.DRAW_INVERT, ShapeMode.SHAPE_RECT, 0.0f);
                        }
                    }
                }
                   
            }
                
            CameraServer.getInstance().setImage(rawImage);
            
            targets.sort(null);
            

//                //Zero out scores and set verticalIndex to first target in case there are no horizontal targets
//                target.totalScore = target.leftScore = target.rightScore = target.tapeWidthScore = target.verticalScore = 0;
//                target.verticalIndex = verticalTargets[0];
//                for (int i = 0; i < verticalTargetCount; i++) {
//                    ParticleAnalysisReport verticalReport = filteredImage.getParticleAnalysisReport(verticalTargets[i]);
//                    for (int j = 0; j < horizontalTargetCount; j++) {
//                        ParticleAnalysisReport horizontalReport = filteredImage.getParticleAnalysisReport(horizontalTargets[j]);
//                        double horizWidth, horizHeight, vertWidth, leftScore, rightScore, tapeWidthScore, verticalScore, total;
//
//                        //Measure equivalent rectangle sides for use in score calculation
//                        horizWidth = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_LONG_SIDE);
//                        vertWidth = NIVision.MeasureParticle(filteredImage.image, verticalTargets[i], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
//                        horizHeight = NIVision.MeasureParticle(filteredImage.image, horizontalTargets[j], false, MeasurementType.IMAQ_MT_EQUIVALENT_RECT_SHORT_SIDE);
//
//                        //Determine if the horizontal target is in the expected location to the left of the vertical target
//                        leftScore = ratioToScore(1.2 * (verticalReport.boundingRectLeft - horizontalReport.center_mass_x) / horizWidth);
//                        //Determine if the horizontal target is in the expected location to the right of the  vertical target
//                        rightScore = ratioToScore(1.2 * (horizontalReport.center_mass_x - verticalReport.boundingRectLeft - verticalReport.boundingRectWidth) / horizWidth);
//                        //Determine if the width of the tape on the two targets appears to be the same
//                        tapeWidthScore = ratioToScore(vertWidth / horizHeight);
//                        //Determine if the vertical location of the horizontal target appears to be correct
//                        verticalScore = ratioToScore(1 - (verticalReport.boundingRectTop - horizontalReport.center_mass_y) / (4 * horizHeight));
//                        total = leftScore > rightScore ? leftScore : rightScore;
//                        total += tapeWidthScore + verticalScore;
//
//                        //If the target is the best detected so far store the information about it
//                        if (total > target.totalScore) {
//                            target.horizontalIndex = horizontalTargets[j];
//                            target.verticalIndex = verticalTargets[i];
//                            target.totalScore = total;
//                            target.leftScore = leftScore;
//                            target.rightScore = rightScore;
//                            target.tapeWidthScore = tapeWidthScore;
//                            target.verticalScore = verticalScore;
//                        }
//                    }
//                    //Determine if the best target is a Hot target
//                    target.Hot = isHot(target);
//                }
              
//            filteredImage.free();
//            thresholdImage.free();
//            image.free();
            
        } 
        catch (VisionException ex)
        {
            Log.recoverable("RoboVision", ex.getMessage());
            ex.printStackTrace();
        }
        
        return targets;
    }
    /**
     * Computes a score (0-100) comparing the aspect ratio to the ideal aspect
     * ratio for the target. This method uses the equivalent rectangle sides to
     * determine aspect ratio as it performs better as the target gets skewed by
     * moving to the left or right. The equivalent rectangle is the rectangle
     * with sides x and y where particle area= x*y and particle perimeter= 2x+2y
     *
     * @param report The Particle Analysis Report for the particle, used for the
     * width, height, and particle number
     * 
     * @return The aspect ratio score (0-100)
     */
    public static double scoreAspectRatio(ParticleReport report, double idealAspectRatio)
    {

        return ratioToScore(report.boundingRectWidth / report.boundingRectHeight / idealAspectRatio);
    }
    
    /**
     * Converts a ratio with ideal value of 1 to a score. The resulting function
     * is piecewise linear going from (0,0) to (1,100) to (2,0) and is 0 for all
     * inputs outside the range 0-2
     */
    public static double ratioToScore(double ratio) 
    {
        return (Math.max(0, Math.min(100 * (1 - Math.abs(1 - ratio)), 100)));
    }

}
