package org.team3128.autonomous.commands;

import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.util.RobotMath;
import org.team3128.util.Units;

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
 * Command to move forward or backward to a certain ultrasonic distance.
 */
public class CmdMoveUltrasonic extends Command {

	double _cm;
	
	int _msec;
	
	double _threshold;
	
	long startTime;
	
	/**
	 * @param cm how far on the ultrasonic to move.
	 * @param threshold acceptible threshold from desired distance in cm
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdMoveUltrasonic(double cm, double threshold, int msec)
    {
    	_cm = cm;
    	
    	if(cm < 0)
    	{
    		throw new IllegalArgumentException("Distance cannot be negative!");
    	}
    	
    	_msec = msec;
    }

    protected void initialize()
    {
		AutoUtils.clearEncoders();
		startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			AutoUtils.killRobot("Move Overtime");
		}
		
		int norm = (int) RobotMath.sgn((AutoHardware.ultrasonic.getRangeMM() * Units.MM) - _cm);
		
		AutoHardware._leftMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
		AutoHardware._rightMotors.setControlTarget(AutoUtils.speedMultiplier * .25 * norm);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return ((AutoHardware.ultrasonic.getRangeMM() * Units.MM) - _cm) < _threshold;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		AutoUtils.stopMovement();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
