package org.team3128.hardware.encoder.distance;

public interface IDistanceEncoder
{
	public void clear();
	
	/**
	 * Get distance traveled in degrees since last reading
	 */
	public double getDistance();
}
