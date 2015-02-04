package org.team3128.autonomous.commands;

import static java.lang.Math.abs;

import org.team3128.Log;
import org.team3128.autonomous.AutoHardware;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to move forward the given amount of centimeters
 */
public class CmdMoveForward extends Command {

	int _cm;
	
	int _msec;
	
	long startTime;
	
	/**
	 * encoder pulses that the move will take
	 */
	int enc;
	
	boolean rightDone = false;
	
	boolean leftDone = false;
	
	/**
	 * @param cm how far to move.  Accepts negative values.
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdMoveForward(int cm, int msec)
    {
    	_cm = cm;
    	
    	_msec = msec;
    }

    protected void initialize()
    {
		AutoHardware.clearEncoders();
		enc = RobotMath.floor_double_int(abs(AutoHardware.cmToDegrees(_cm)));
		int norm = RobotMath.sgn(_cm);
		startTime = System.currentTimeMillis();

		
		AutoHardware._leftMotors.setControlTarget(AutoHardware.speedMultiplier * .25 * norm);
		AutoHardware._rightMotors.setControlTarget(AutoHardware.speedMultiplier * .25 * norm);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			AutoHardware.killRobot("Move Overtime");
		}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
    	leftDone = AutoHardware._encLeft.getDistance() > enc;
    	rightDone = AutoHardware._encRight.getDistance() > enc;
    	
        return leftDone || rightDone;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		AutoHardware.stopMovement();
    	
    	if(leftDone)
    	{
    		Log.debug("CmdMoveForward", "The right side went at " + ((AutoHardware._encRight.getDistance() * 100.0) / AutoHardware._encRight.getDistance()) + "% of the left side");
    	}
    	else
    	{
    		Log.debug("CmdMoveForward", "The left side went at " + ((AutoHardware._encLeft.getDistance() * 100.0) / AutoHardware._encLeft.getDistance()) + "% of the right side");
    	}
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
