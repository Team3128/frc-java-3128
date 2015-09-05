package org.team3128.hardware.ultrasonic;

import org.team3128.util.Units;

import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Wrapper around the WPILib Ultrasonic class.
 * 
 * Used for encoders with a ping input line and a echo return line.
 * @author Jamie
 *
 */
public class DigitalUltrasonic extends IUltrasonic
{
	private Ultrasonic ultrasonic;
	
	private double maxDistance;
	
	/**
	 * Construct a DigitalUltrasonic.
	 * @param pingChannel the number of the first (ping) DIO channel
	 * @param echoChannel the number of the second (echo) DIO channel
	 * @param maxDistance the maximum distance, in CM, that the sensor can sense objects at.
	 */
	public DigitalUltrasonic(int pingChannel, int echoChannel, double maxDistance)
	{
		ultrasonic = new Ultrasonic(pingChannel, echoChannel);
		
		this.maxDistance = maxDistance;
	}
	
	@Override
	public double getDistance()
	{
		return ultrasonic.getRangeMM() * Units.mm;
	}

	@Override
	public double getMaxDistance()
	{
		return maxDistance;
	}
	
	@Override
	public boolean canSeeAnything()
	{
		return ultrasonic.isRangeValid() && super.canSeeAnything();
	}
}
