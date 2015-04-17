package org.team3128.autonomous;

import org.team3128.Log;

import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * Various functions used in the autonomous code.
 * @author Jamie
 *
 */
public class AutoUtils
{

	public static void clearEncoders()
	{
		AutoHardware.encLeft.clear();
		AutoHardware.encRight.clear();
	}

	public static void stopMovement()
	{
		AutoHardware.leftMotors.setControlTarget(0);
		AutoHardware.rightMotors.setControlTarget(0);
	}

	public static void killRobot(String cause)
	{
		Log.recoverable("AutoUtils", "Robot killed by autonomous error: " + cause);

		AutoHardware.leftMotors.setControlTarget(0);
		AutoHardware.rightMotors.setControlTarget(0);
		AutoHardware.clawArm.switchArmToManualControl();
		AutoHardware.clawArm.switchJointToManualControl();
		AutoHardware.clawArm.resetTargets();
		
		Scheduler.getInstance().disable();
		
		//BAD, KILLS ROBOT
		//throw new RuntimeException("Error in automatic movement - " + cause + "\nRobot shut down!");
	}

	//convert motor power to RPM
	public static final double speedMultiplier = 1;

}
