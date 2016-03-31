package org.team3128.hardware.ultrasonic;

import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Wrapper around the WPILib Ultrasonic class.
 * 
 * Used for ultrasonics with a ping input line and a echo return line.
 * @author Jamie
 *
 */
public class DigitalUltrasonic implements IUltrasonic
{
	private Ultrasonic ultrasonic;
	
	private double maxDistance;

	private boolean autoPing = true;
	
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
		if(!autoPing)
		{
			ultrasonic.ping();
			
			//wait for ping to be received
			try
			{
				Thread.sleep(2);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
			
		return ultrasonic.getRangeMM() * Length.mm;
	}

	@Override
	public double getMaxDistance()
	{
		return maxDistance;
	}
	
	@Override
	public boolean canSeeAnything()
	{
		return ultrasonic.isRangeValid() && getDistance() < maxDistance;
	}

	@Override
	public void setAutoPing(boolean autoPing)
	{
		this.autoPing = autoPing;
		
		ultrasonic.setAutomaticMode(autoPing);
	}
}
