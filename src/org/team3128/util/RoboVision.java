package org.team3128.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.team3128.Log;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.MeasurementType;
import com.ni.vision.NIVision.ROI;

import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
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
	//Minimum area of particles to be considered
    static final int AREA_MINIMUM = 150;
    //Maximum number of particles to process
    static final int MAX_PARTICLES = 10;

    
    public static void targetRecognition(AxisCamera camera) 
    {
       
        try {
            /**
             * Do the image capture with the MainTheClawwww.camera and apply the algorithm
             * described above. This sample will either get images from the
             * MainTheClawwww.camera or from an image file stored in the top level directory in
             * the flash memory on the cRIO. The file name in this case is
             * "testImage.jpg"
             *
             */
            ColorImage image;
            BinaryImage thresholdImage, filteredImage;
            image = camera.getImage();
            //DO NOT CHANGE THESE VALUES BELOW.
            thresholdImage = image.thresholdHSV(105, 137, 230, 255, 73, 183);   // keep only green objects
           
            //DebugLog.log(DebugLog.LVL_INFO, "Vision", "Just finished thresholding");
            //thresholdImage.write("/threshold.bmp");
            filteredImage = thresholdImage;
            //filteredImage.write("/filteredImage.bmp");

            //iterate through each particle and score to see if it is a target

            if (filteredImage.getNumberParticles() > 0) {
                for (int i = 0; i <= MAX_PARTICLES && i < filteredImage.getNumberParticles(); i++) {
                    ParticleAnalysisReport report = filteredImage.getParticleAnalysisReport(i);

                    //Score each particle on rectangularity and aspect ratio
                    double rectangularity = scoreRectangularity(report);
                    double aspectRatioVertical = scoreAspectRatio(filteredImage, report, i, true);
                    double aspectRatioHorizontal = scoreAspectRatio(filteredImage, report, i, false);

//                    //Check if the particle is a horizontal target, if not, check if it's a vertical target
//                    if (scoreCompare(scores[i], false)) {
//                        System.out.println("particle: " + i + "is a Horizontal Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
//                        horizontalTargets[horizontalTargetCount++] = i; //Add particle to target array and increment count
//                    } else if (scoreCompare(scores[i], true)) {
//                        System.out.println("particle: " + i + "is a Vertical Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
//                        verticalTargets[verticalTargetCount++] = i;  //Add particle to target array and increment count
//                    } else {
//                        System.out.println("particle: " + i + "is not a Target centerX: " + report.center_mass_x + "centerY: " + report.center_mass_y);
//                    }
                    System.out.println("rect: " + rectangularity + " ARHoriz: " + aspectRatioHorizontal + " ARVert: " + aspectRatioVertical);
                    System.out.println();
                }

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
        } catch (NIVisionException ex) {
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
    public static double scoreAspectRatio(BinaryImage image, ParticleAnalysisReport report, int particleNumber, boolean vertical) throws NIVisionException
    {
        double rectLong, rectShort, aspectRatio, idealAspectRatio;

        rectLong = NIVision.imaqMeasureParticle(image.image, particleNumber, 0, NIVision.MeasurementType.MT_EQUIVALENT_RECT_LONG_SIDE);
        rectShort = NIVision.imaqMeasureParticle(image.image, particleNumber, 0, MeasurementType.MT_EQUIVALENT_RECT_SHORT_SIDE);
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
    public static double scoreRectangularity(ParticleAnalysisReport report) {
        if (report.boundingRectWidth * report.boundingRectHeight != 0) {
            return 100 * report.particleArea / (report.boundingRectWidth * report.boundingRectHeight);
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
     * @param outer True if the particle should be treated as an outer target,
     * false to treat it as a center target
     *
     * @return True if the particle meets all limits, false otherwise
     */
    public static boolean isTarget(double rectangularity, double asRatioHoriz, double asRationVert, boolean vertical) {
        boolean isTarget = true;

        isTarget &= rectangularity > RECTANGULARITY_LIMIT;
        if (vertical) {
            isTarget &= asRatioHoriz > ASPECT_RATIO_LIMIT;
        } else {
            isTarget &= asRationVert > ASPECT_RATIO_LIMIT;
        }

        return isTarget;
    }
    
    static Constructor<BinaryImage> imageConstructor;
    static Constructor<ROI> roiConstructor;
    
    protected static BinaryImage particleFilterThatDoesntCrash(BinaryImage sourceImage, NIVision.ParticleFilterCriteria2[] criteria)
    {
    	if(imageConstructor == null)
    	{
    		try
			{
				imageConstructor = BinaryImage.class.getDeclaredConstructor(new Class[0]);
				roiConstructor = ROI.class.getDeclaredConstructor(new Class[0]);
			} catch (NoSuchMethodException | SecurityException e)
			{
				e.printStackTrace();
			}
    		imageConstructor.setAccessible(true);
    		roiConstructor.setAccessible(true);
    	}
    	
    	//create a new image and roi using reflection
    	BinaryImage result = null;
    	ROI roi = null;
		try
		{
			result = imageConstructor.newInstance();
	    	roi = roiConstructor.newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
    	
        NIVision.ParticleFilterOptions2 options = new NIVision.ParticleFilterOptions2(0, 0, 0, 1);
        NIVision.imaqParticleFilter4(result.image, sourceImage.image, criteria, options, roi);
        options.free();
        return result;
    }


}
