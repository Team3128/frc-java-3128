package org.team3128.hardware.ultrasonic;

/**
 * Interface for ultrasonic sensors, which measure the distance to the object in front of them using sound waves.
 * @author Jamie
 *
 */
public interface IUltrasonic
{
	/**
	 * Gets the distance between the sensor and the thing in front of it in cm.
	 * 
	 * If Auto-Ping is on, this is assumed to be a cheap operation. otherwise it can be expensive.
	 * @return
	 */
	public abstract double getDistance();
	
	/**
	 * 
	 * @return true if the sensor can "see" any objects.
	 */
	public default boolean canSeeAnything()
	{
		return getDistance() <= getMaxDistance();
	}

	/**
	 * Get the longest distance that the sensor can sense.
	 * @return
	 */
	public abstract double getMaxDistance();
	
	/**
	 * Control whether the sensor will ping in the background.  Otherwise, it will only ping when getDistance() is called.
	 */
	public abstract void setAutoPing(boolean autoPing);
}
