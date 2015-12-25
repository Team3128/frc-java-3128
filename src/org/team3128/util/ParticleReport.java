package org.team3128.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.GetImageSizeResult;

/**
 * Class to hold data about a vision particle.  Taken from the 2015 vision example.
 * @author Jamie
 *
 */
public class ParticleReport implements Comparable<ParticleReport>{
	public double percentAreaToImageArea;
	public double area;
	public int boundingRectLeft;
	public int boundingRectTop;
	public int boundingRectRight;
	public int boundingRectBottom;
	
	public int boundingRectHeight, boundingRectWidth;
	
	public int center_of_mass_x, center_of_mass_y;
	
	GetImageSizeResult imageSize;
	
    /**
     * A score (0-100) estimating how rectangular the particle is by
     * comparing the area of the particle to the area of the bounding box
     * surrounding it. A perfect rectangle would cover the entire bounding box.
     */
	public double rectangularity;
		
	private double score;
	
	 //TODO: make this a config value
	static final double VIEW_ANGLE = 64; //View angle for camera, 49.4 Axis m1011 camera, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480

	
	/**
	 * Populate from image and index in image
	 * @param image
	 * @param particleIndex
	 */
	public ParticleReport(NIVision.Image image, int particleIndex, GetImageSizeResult imageSize)
	{
		this.imageSize = imageSize;
		
		percentAreaToImageArea = NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
		area = NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
		boundingRectTop = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
		boundingRectLeft = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
		boundingRectBottom = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
		boundingRectRight = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
		
		boundingRectWidth = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_WIDTH);
		boundingRectHeight = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_HEIGHT);
		
        center_of_mass_x = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_CENTER_OF_MASS_X);
        center_of_mass_y = (int) NIVision.imaqMeasureParticle(image, particleIndex, 0, NIVision.MeasurementType.MT_CENTER_OF_MASS_Y);
        
        if (boundingRectWidth * boundingRectHeight != 0) {
            rectangularity = 100 * (double) area / ((double) boundingRectWidth * boundingRectHeight);
        } else {
            rectangularity = 0;
        }
	}
	
	@Override
	public int compareTo(ParticleReport r)
	{
		return this.score < r.score ? -1 : this.score > r.score ? 1 : 0;
	}
	
	/**
	 * Computes the estimated distance to a target using the width of the particle in the image. For more information and graphics
	 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
	 *                                               ^^^^^^^^^^^^^^^^^^^^^^^^^
	 *                                               HAH!  Still says that it is coming soon! -Jamie
	
     * @return The estimated distance to the target in cm.
	 */
	public double computeDistance()
	{
		double normalizedWidth, targetWidth;

		normalizedWidth = 2*(boundingRectRight - boundingRectLeft)/imageSize.width;
		targetWidth = 7;

		return  targetWidth/(normalizedWidth*12*Math.tan(VIEW_ANGLE*Math.PI/(180*2)));
	}
	
	/**
	 * Set the score of the particle, which is its suitability according to whatever processing algorithm you're using.
	 * This value will be used to sort ParticleReports in a list
	 * @param score
	 */
	void setScore(double score)
	{
		this.score = score;
	}
	
	double getScore()
	{
		return score;
	}
	
	
	//these numbers are constant, so to get better performance we calculate them beforehand
	final static private double HALF_VIEW_ANGLE = RobotMath.dTR(VIEW_ANGLE) / 2.0; //in radians

	final static private double TANGENT_OF_HALF_VIEW_ANGLE = Math.tan(HALF_VIEW_ANGLE); //in radians
	
	/**
	 * Get this particle's offset in degrees from directly in front of the camera.
	 *        _
	 *       |_|
	 *     \    |    /
	 *      \   |   /
	 *       \  |  /
	 *        \ | /
	 *         \_/
	 *         | |
	 * Can be positive or negative.  If the object is in the center, it returns 0.
	 * @return
	 */
	public double getHeadingAngleOffset()
	{
		double halfImageWidth = imageSize.width / 2.0;
		double distanceFromCenter = center_of_mass_x - halfImageWidth;
		
		double headingRadians = Math.atan(distanceFromCenter * TANGENT_OF_HALF_VIEW_ANGLE / halfImageWidth);
		
		return RobotMath.rTD(headingRadians);
	}
	
}
