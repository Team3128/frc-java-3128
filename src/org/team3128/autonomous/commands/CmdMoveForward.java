package org.team3128.autonomous.commands;

import static java.lang.Math.abs;

import org.team3128.Log;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to move forward the given amount of centimeters
 */
public class CmdMoveForward extends Command {

	double _cm;
	
	int _msec;
	
	long startTime;
	
	/**
	 * rotations that the move will take
	 */
	double enc;
	
	boolean rightDone = false;
	
	boolean leftDone = false;
	
	/**
	 * @param d how far to move.  Accepts negative values.
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdMoveForward(double d, int msec)
    {
    	_cm = d;
    	
    	_msec = msec;
    }

    protected void initialize()
    {
		AutoUtils.clearEncoders();
		enc = abs(RobotMath.cmToRotations(_cm));
		int norm = (int) RobotMath.sgn(_cm);
		startTime = System.currentTimeMillis();

		
		AutoHardware.leftMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
		AutoHardware.rightMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			AutoUtils.killRobot("Move Overtime");
		}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
    	leftDone = Math.abs(AutoHardware.encLeft.getDistanceInDegrees()) > enc;
    	rightDone = Math.abs(AutoHardware.encRight.getDistanceInDegrees()) > enc;
    	
        return leftDone || rightDone;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		AutoUtils.stopMovement();
    	
    	if(leftDone)
    	{
    		Log.debug("CmdMoveForward", "The right side went at " + ((AutoHardware.encRight.getDistanceInDegrees() * 100.0) / AutoHardware.encRight.getDistanceInDegrees()) + "% of the left side");
    	}
    	else
    	{
    		Log.debug("CmdMoveForward", "The left side went at " + ((AutoHardware.encLeft.getDistanceInDegrees() * 100.0) / AutoHardware.encLeft.getDistanceInDegrees()) + "% of the right side");
    	}
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
