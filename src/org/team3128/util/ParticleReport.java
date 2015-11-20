package org.team3128.util;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

/**
 * Class to hold data about a vison particle.  Taken from the 2015 vision example.
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
	
    /**
     * A score (0-100) estimating how rectangular the particle is by
     * comparing the area of the particle to the area of the bounding box
     * surrounding it. A perfect rectangle would cover the entire bounding box.
     */
	public double rectangularity;
	
	private Image sourceImage;
	
	private double score;
	
	//TODO: make this a config value
	double VIEW_ANGLE = 64; //View angle for camera, 49.4 Axis m1011 camera, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480

	
	/**
	 * Populate from image and index in image
	 * @param image
	 * @param particleIndex
	 */
	public ParticleReport(NIVision.Image image, int particleIndex)
	{
		sourceImage = image;
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
            rectangularity = 100 * area / (boundingRectWidth * boundingRectHeight);
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
	public double computeDistance() {
		double normalizedWidth, targetWidth;
		NIVision.GetImageSizeResult size;

		size = NIVision.imaqGetImageSize(sourceImage);
		normalizedWidth = 2*(boundingRectRight - boundingRectLeft)/size.width;
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
	
}
