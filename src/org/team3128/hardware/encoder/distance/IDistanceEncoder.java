package org.team3128.hardware.encoder.distance;

/**
 * Generic interface for an encoder which measures distance.
 * @author Jamie
 *
 */
public interface IDistanceEncoder
{
	public void clear();
	
	/**
	 * Get distance traveled in degrees since last reading
	 */
	public double getDistanceInDegrees();
}
