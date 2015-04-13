package org.team3128.autonomous.commands;

import static java.lang.Math.abs;

import org.team3128.Options;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.Direction;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to to an arc turn in the specified amount of degrees.
 * 
 * Sets the opposite motors from the direction provided, so turning LEFT would set the RIGHT motors.
 */
public class CmdInPlaceTurn extends Command {

	float _degs;
	
	int _msec;
	
	long startTime;
	
	/**
	 * rotations that the move will take
	 */
	double enc;
	
	IDistanceEncoder sideEncoder;
	
	MotorGroup forwardMotors;
	
	MotorGroup backwardMotors;
	
	/**
	 * @param degs how far to turn in degrees.  Accepts negative values.
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
    public CmdInPlaceTurn(float degs, int msec, Direction dir)
    {
    	_degs = degs;
    	
    	_msec = msec;
    	
    	if(dir == Direction.RIGHT)
    	{
    		sideEncoder = AutoHardware.encLeft;
    		forwardMotors = AutoHardware.leftMotors;
    		backwardMotors = AutoHardware.rightMotors;
    	}
    	else
    	{
    		sideEncoder = AutoHardware.encRight;
    		forwardMotors = AutoHardware.rightMotors;
    		backwardMotors = AutoHardware.leftMotors;
    	}
    }

    protected void initialize()
    {
		enc = RobotMath.floor_double_int(RobotMath.cmToRotations((Math.PI*Options.wheelBase)*(abs(_degs)/360.0)));
		AutoUtils.clearEncoders();
		forwardMotors.setControlTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .5);
		backwardMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * RobotMath.sgn(_degs)* .5);
		startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			AutoUtils.killRobot("Arc Turn Overtime");
		}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
        return Math.abs(sideEncoder.getDistanceInDegrees()) >= enc;
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
