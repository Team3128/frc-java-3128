package org.team3128.autonomous;

import org.team3128.Options;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorLink;

public class AutoHardware
{
	public static IDistanceEncoder _encLeft;
	public static IDistanceEncoder _encRight;
	
	public static MotorLink _leftMotors;
	public static MotorLink _rightMotors;
	
	//convert motor power to RPM
	public static final double speedMultiplier = 5310;
	
	public static void clearEncoders()
	{
		_encLeft.clear();
		_encRight.clear();
	}
	
	public static double cmToDegrees(double cm)
	{
		return Options.instance()._degreesPercm * cm;
	}
	
	public static void stopMovement()
	{
		_leftMotors.setControlTarget(0);
		_rightMotors.setControlTarget(0);
	}
	
	
	/**
	 * quick sleep function so you don't have to catch an InterruptedException
	 * @param msec
	 */
	public static void sleep(int msec)
	{
		try
		{
			Thread.sleep(msec);
		} 
		catch (InterruptedException e)
		{
			return;
		}
	}
	
	public static void killRobot(String cause)
	{
		throw new RuntimeException("Error in automatic movement - " + cause + "\nRobot shut down!");
	}
}
