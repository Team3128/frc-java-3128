package org.team3128.hardware.ultrasonic;

/**
 * Interface for ultrasonic sensors, which measure the distance to the object in front of them using sound waves.
 * @author Jamie
 *
 */
public abstract class IUltrasonic
{
	/**
	 * Gets the distance between the sensor and the thing in front of it in cm.
	 * @return
	 */
	public abstract double getDistance();
	
	/**
	 * 
	 * @return true if the sensor can "see" any objects.
	 */
	public boolean canSeeAnything()
	{
		return getDistance() >= getMaxDistance();
	}

	/**
	 * Get the longest distance that the sensor can sense.
	 * @return
	 */
	public abstract double getMaxDistance();
}
