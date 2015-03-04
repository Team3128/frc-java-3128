package org.team3128.hardware.encoder.velocity;

import org.team3128.Log;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.util.RobotMath;

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

/**
 * Class to use an angular encoder as a velocity encoder.
 * 
 * It uses a thread to regularly check the angle and calculate the encoder's speed.
 * @author Narwhal
 *
 */
public class AngleVelEncoder implements IVelocityEncoder 
{
	IAngularEncoder _angleEncoder;
	
	private double lastSpeed = 0;
	
	private double lastAngle;
	
	private Thread _thread;
	
	private int sleepTime;
	
	private Runnable updateThread = () -> 
	{
		long lastUpdateTime = System.currentTimeMillis();
		while(true)
		{
			
			try
			{
				Thread.sleep(sleepTime - 8);
			}
			catch(InterruptedException error)
			{
				return;
			}
			
			long updateTime = System.currentTimeMillis();
			double newAngle = _angleEncoder.getAngle();
			
			if((updateTime - lastUpdateTime) > sleepTime)
			{
				lastAngle = newAngle;
				//thread scheduler got to us too late
				Log.debug("AngleVelEncoder", "Slept " + (updateTime - lastUpdateTime) + " milliseconds too long");
				
				lastUpdateTime = updateTime;

			}
			
			double degreesChanged = RobotMath.angleDistance(lastAngle, newAngle, true);
			
			if(degreesChanged < .01)
			{
				degreesChanged = 0;
			}
			lastUpdateTime = updateTime;
			
			if(degreesChanged != 0)
			{
				System.out.println();
			}
			
			lastSpeed = (degreesChanged / 360.0) * (60000 / (sleepTime - 7));
			
		}
	};
	
	/**
	 * 
	 * @param angleEncoder
	 * @param maxRPM The fastest that you expect the encoder to move.  Used to calculate the update frequency.
	 */
	public AngleVelEncoder(IAngularEncoder angleEncoder, double maxRPM)
	{
		_angleEncoder = angleEncoder;
		
		lastAngle = angleEncoder.getAngle();
		
		sleepTime = RobotMath.floor_double_int(30000 / maxRPM);
		
		_thread = new Thread(updateThread);
		_thread.start();
	}

	@Override
	public double getSpeedInRPM()
	{
		return lastSpeed;
	}
}
