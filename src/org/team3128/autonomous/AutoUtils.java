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


	/**
	 * Things that call this should stop any motors that were running first!
	 * @param cause
	 */
	public static void killRobot(String cause)
	{
		Log.fatal("AutoUtils", "Robot killed by autonomous error: " + cause);
		
		//stop more commands from being run
		Scheduler.getInstance().disable();
	
		
		//BAD, KILLS ROBOT
		//throw new RuntimeException("Error in automatic movement - " + cause + "\nRobot shut down!");
	}

	//convert motor power to RPM
	public static final double speedMultiplier = 1;

}
