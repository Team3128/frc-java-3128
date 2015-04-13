package org.team3128.autonomous.commands;

import static java.lang.Math.abs;

import org.team3128.Options;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.Direction;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to to an arc turn in the specified amount of degrees.
 * 
 * Sets the opposite motors from the direction provided, so turning LEFT would set the RIGHT motors.
 */
public class CmdArcTurn extends Command {

	float _degs;
	
	int _msec;
	
	long startTime;
	
	/**
	 * rotations that the move will take
	 */
	double enc;
	
	QuadratureEncoderLink sideEncoder;
	
	QuadratureEncoderLink otherSideEncoder;
	
	MotorGroup sideMotors;
	
	MotorGroup otherSideMotors;
	
	/**
	 * @param degs how far to turn in degrees.  Accepts negative values.
	 * @param msec How long the move should take. If set to 0, do not time the move.
	 */
    public CmdArcTurn(float degs, int msec, Direction dir)
    {
    	_degs = degs;
    	
    	_msec = msec;
    	
    	if(dir == Direction.RIGHT)
    	{
    		sideEncoder = AutoHardware.encLeft;
    		otherSideEncoder = AutoHardware.encRight;
    		
    		sideMotors = AutoHardware.leftMotors;
    		otherSideMotors = AutoHardware.rightMotors;
    	}
    	else
    	{
    		sideEncoder = AutoHardware.encRight;
    		otherSideEncoder = AutoHardware.encLeft;
    		
    		sideMotors = AutoHardware.rightMotors;
    		otherSideMotors = AutoHardware.leftMotors;
    	}
    }

    protected void initialize()
    {
		enc = RobotMath.cmToRotations((2.0*Math.PI*Options.wheelBase)*(abs(_degs)/360.0));
		AutoUtils.clearEncoders();
		
		sideMotors.setControlTarget(AutoUtils.speedMultiplier * RobotMath.sgn(_degs) * .25);
		startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
		if(_msec != 0 && System.currentTimeMillis() - startTime >_msec)
		{
			//AutoUtils.killRobot("Arc Turn Overtime", );
		}
		
		otherSideMotors.setControlTarget(-1 * RobotMath.getEstMotorPowerForRPM(otherSideEncoder.getSpeedInRPM()));
		
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
    	//System.out.println(sideEncoder.getDistance());
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
