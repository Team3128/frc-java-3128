package org.team3128.util;

import org.team3128.Log;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.MeasurementType;
import com.ni.vision.NIVision.Range;
import com.ni.vision.NIVision.ShapeMode;
import com.ni.vision.VisionException;

import edu.wpi.first.wpilibj.CameraServer;




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
	//Minimum area of particles to be considered
    static final int AREA_MINIMUM = 150;
    //Maximum number of particles to process
    static final int MAX_PARTICLES = 10;

    Image rawImage;
    Image thresholdedImage;
    Image filteredImage;
    
    NIVision.ParticleFilterCriteria2[] filterCriteria;
    
    public static class Scores {
        double rectangularity;
        double aspectRatioVertical;
        double aspectRatioHorizontal;
    }
    
    public RoboVision()
    {
    	rawImage = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
    	thresholdedImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
    	filteredImage = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
    	
    	filterCriteria = new NIVision.ParticleFilterCriteria2[1];
    	filterCriteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM, Short.MAX_VALUE, 0, 0);
    }
   
    
    public void targetRecognition(int cameraNum) 
    {
       
        try {
            /**
             * Do the image capture with the camera and apply the algorithm
             * described above.
             */
            NIVision.IMAQdxGrab(cameraNum, rawImage, 1);
            //DO NOT CHANGE THESE VALUES BELOW.
            NIVision.imaqColorThreshold(thresholdedImage, rawImage, 255, ColorMode.HSV, new Range(105, 137), new Range(230, 255), new Range(73, 183)); // keep only green objects
           
            Log.debug("RoboVision", "Just finished thresholding");
            //thresholdImage.write("/threshold.bmp");
            //NIVision.imaqWriteBMPFile(thresholdedImage, "/home/lvuser/RoboVision/thresholdedImage.bmp", 0, new RGBValue(255, 255, 255, 1));

            //find particles
            NIVision.imaqParticleFilter4(filteredImage, thresholdedImage, filterCriteria, new NIVision.ParticleFilterOptions2(0,0,1,1), null);
            
            //iterate through each particle and score to see if it is a target
            int numParticles = NIVision.imaqCountParticles(filteredImage, 1);
            
            Scores scores[] = new Scores[numParticles];
            int horizontalTargetCount = 0, verticalTargetCount = 0;
            
            int verticalTargets[] = new int[MAX_PARTICLES];
            int horizontalTargets[] = new int[MAX_PARTICLES];
            
            if(NIVision.imaqCountParticles(filteredImage, 1) > 0) {
                for (int i = 0; i <= MAX_PARTICLES && i < NIVision.imaqCountParticles(filteredImage, 1); i++) {
                    ParticleReport report = new ParticleReport(filteredImage, i);

                    //Score each particle on rectangularity and aspect ratio
                    double rectangularity = scoreRectangularity(report);
                    double aspectRatioVertical = scoreAspectRatio(thresholdedImage, report, i, true);
                    double aspectRatioHorizontal = scoreAspectRatio(thresholdedImage, report, i, false);

                    //Check if the particle is a horizontal target, if not, check if it's a vertical target
                    if (isTarget(scores[i], false)) {
                        System.out.println("particle: " + i + "is a Horizontal Target centerX: " + report.center_of_mass_x + "centerY: " + report.center_of_mass_y);
                        horizontalTargets[horizontalTargetCount++] = i; //Add particle to target array and increment count
                    }
                    else if (isTarget(scores[i], true)) {
                        System.out.println("particle: " + i + "is a Vertical Target centerX: " + report.center_of_mass_x + "centerY: " + report.center_of_mass_x);
                        verticalTargets[verticalTargetCount++] = i;  //Add particle to target array and increment count
                        //draw on image
                        NIVision.Rect rect = new NIVision.Rect(report.boundingRectTop, report.boundingRectLeft, report.boundingRectHeight, report.boundingRectWidth);
                        NIVision.imaqDrawShapeOnImage(rawImage, rawImage, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, 0.0f);
                        
                    } else {
                        System.out.println("particle: " + i + "is not a Target centerX: " + report.center_of_mass_x + "centerY: " + report.center_of_mass_x);
                    }
                    System.out.println("rect: " + rectangularity + " ARHoriz: " + aspectRatioHorizontal + " ARVert: " + aspectRatioVertical);
                    System.out.println();
                    
                   
                }
                
                CameraServer.getInstance().setImage(rawImage);

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
              }
//            filteredImage.free();
//            thresholdImage.free();
//            image.free();
        } catch (VisionException ex) {
            Log.recoverable("RoboVision", ex.getMessage());
            ex.printStackTrace();
        }
    }
    /**
     * Computes a score (0-100) comparing the aspect ratio to the ideal aspect
     * ratio for the target. This method uses the equivalent rectangle sides to
     * determine aspect ratio as it performs better as the target gets skewed by
     * moving to the left or right. The equivalent rectangle is the rectangle
     * with sides x and y where particle area= x*y and particle perimeter= 2x+2y
     *
     * @param image The image containing the particle to score, needed to
     * perform additional measurements
     * @param report The Particle Analysis Report for the particle, used for the
     * width, height, and particle number
     * @param outer Indicates whether the particle aspect ratio should be
     * compared to the ratio for the inner target or the outer
     * @return The aspect ratio score (0-100)
     */
    public static double scoreAspectRatio(Image image, ParticleReport report, int particleNumber, boolean vertical)
    {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.imaqMeasureParticle(image, particleNumber, 0, NIVision.MeasurementType.MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.imaqMeasureParticle(image, particleNumber, 0, MeasurementType.MT_EQUIVALENT_RECT_SHORT_SIDE);
        idealAspectRatio = vertical ? (4.0 / 32) : (23.5 / 4);      //Vertical reflector 4" wide x 32" tall, horizontal 23.5" wide x 4" tall

        //Divide width by height to measure aspect ratio
        if (report.boundingRectWidth > report.boundingRectHeight) 
        {
            //particle is wider than it is tall, divide long by short
            aspectRatio = ratioToScore((rectLong / rectShort) / idealAspectRatio);
        } else
        {
            //particle is taller than it is wide, divide short by long
            aspectRatio = ratioToScore((rectShort / rectLong) / idealAspectRatio);
        }
        return aspectRatio;
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
    
    /**
     * Computes a score (0-100) estimating how rectangular the particle is by
     * comparing the area of the particle to the area of the bounding box
     * surrounding it. A perfect rectangle would cover the entire bounding box.
     *
     * @param report The Particle Analysis Report for the particle to score
     * @return The rectangularity score (0-100)
     */
    public static double scoreRectangularity(ParticleReport report) {
        if (report.boundingRectWidth * report.boundingRectHeight != 0) {
            return 100 * report.area / (report.boundingRectWidth * report.boundingRectHeight);
        } else {
            return 0;
        }
    }
    
    //Score limits used for target identification
    static final int RECTANGULARITY_LIMIT = 50;
    static final int ASPECT_RATIO_LIMIT = 50;

    /**
     * Compares scores to defined limits and returns true if the particle
     * appears to be a target
     *
     * @return True if the particle meets all limits, false otherwise
     */
    public static boolean isTarget(Scores scores, boolean vertical) {
        boolean isTarget = true;

        isTarget &= scores.rectangularity > RECTANGULARITY_LIMIT;
        if (vertical) {
            isTarget &= scores.aspectRatioHorizontal > ASPECT_RATIO_LIMIT;
        } else {
            isTarget &= scores.aspectRatioVertical > ASPECT_RATIO_LIMIT;
        }

        return isTarget;
    }

}
