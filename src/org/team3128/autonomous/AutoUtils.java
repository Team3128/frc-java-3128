package org.team3128.autonomous;

/**
 * Various functions used in the autonomous code.
 * @author Jamie
 *
 */
public class AutoUtils
{

	public static void clearEncoders()
	{
		AutoHardware._encLeft.clear();
		AutoHardware._encRight.clear();
	}

	public static void stopMovement()
	{
		AutoHardware._leftMotors.setControlTarget(0);
		AutoHardware._rightMotors.setControlTarget(0);
	}

	public static void killRobot(String cause)
	{
		throw new RuntimeException("Error in automatic movement - " + cause + "\nRobot shut down!");
	}

	//convert motor power to RPM
	public static final double speedMultiplier = 5310;

}
