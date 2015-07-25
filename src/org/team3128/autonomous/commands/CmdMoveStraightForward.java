package org.team3128.autonomous.commands;

import static java.lang.Math.abs;

import org.team3128.Log;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/*        _
 *       / \ 
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
 * Command to move forward the given amount of centimeters.
 * 
 * It uses a feedback loop to make the robot drive straight.
 */
public class CmdMoveStraightForward extends Command {

	double _cm;
	
	int _msec;
	
	long startTime;
	
	double kP;
	
	/**
	 * rotations that the move will take
	 */
	double enc;
	
	boolean rightDone = false;
	
	boolean leftDone = false;
	
	double powRight;
	
	/**
	 * @param d how far to move.  Accepts negative values.
	 * @param kP The konstant of proportion.  Scales how the feedback affects the wheel speeds.
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdMoveStraightForward(double d, double kP, int msec)
    {
    	_cm = d;
    	
    	_msec = msec;
    	
    	this.kP = kP;
    	
		enc = abs(RobotMath.cmToRotations(_cm));
		int norm = (int) RobotMath.sgn(_cm);
		powRight = AutoUtils.speedMultiplier * .25 * norm;
    }

    protected void initialize()
    {
		AutoUtils.clearEncoders();
		startTime = System.currentTimeMillis();
		
		AutoHardware.leftMotors.setControlTarget(powRight); //both sides start at the same power
		AutoHardware.rightMotors.setControlTarget(powRight);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//P calculation
    	double error = AutoHardware.encLeft.getSpeedInRPM() -  AutoHardware.encRight.getSpeedInRPM();
    	powRight += kP * error;
		AutoHardware.rightMotors.setControlTarget(powRight);

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

    	Log.debug("CmdMoveStraightForward", "The right side went at " + ((AutoHardware.encRight.getDistanceInDegrees() * 100.0) / AutoHardware.encRight.getDistanceInDegrees()) + "% of the left side");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
