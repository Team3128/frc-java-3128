package org.team3128.autonomous.commands;

import org.team3128.Log;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.AutoUtils;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to stop the robot
 */
public class CmdBrake extends Command 
{
	long startTime;
	
	long _msec;
	
	double _power;
	
	boolean timedOut = false;
	
	boolean leftSideFinished = false;
	
	//sgn of the left side speed
	double leftSideDirection;
	
	boolean rightSideFinished = false;
	
	//sgn of the right side speed
	double rightSideDirection;
	
	/**
	 * 
	 * @param power Motor power to break with.
	 * @param msec
	 */
    public CmdBrake(double power, int msec)
    {
    	_power = power;
    	
    	if(power <= 0)
    	{
    		throw new IllegalArgumentException("The power must be greater than 0!");
    	}
    	
    	_msec = msec;
    }

    protected void initialize()
    {
		startTime = System.currentTimeMillis();
		
		double speedLeft = AutoHardware._encLeft.getSpeedInRPM();
		leftSideDirection = RobotMath.sgn(speedLeft);
		if(Math.abs(speedLeft) > threshold)
		{
			AutoHardware._leftMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * _power * leftSideDirection);
		}
		
		double speedRight = AutoHardware._encRight.getSpeedInRPM();
		rightSideDirection = RobotMath.sgn(speedRight);
		if(Math.abs(speedRight) > threshold)
		{
			AutoHardware._rightMotors.setControlTarget(AutoUtils.speedMultiplier * -1 * _power * rightSideDirection);
		}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//System.out.println(AutoHardware._encLeft.getSpeedInRPM() + " " + AutoHardware._encRight.getSpeedInRPM());
		if(_msec != 0 && (System.currentTimeMillis() > startTime + _msec))
		{
			//one of these commands timing out is not neccesarily fatal
			Log.unusual("CmdBrake", "Timed Out!");
			timedOut = true;
		}
		
		double leftSpeedRPM = AutoHardware._encLeft.getSpeedInRPM();
		if(RobotMath.sgn(leftSpeedRPM) != leftSideDirection || leftSpeedRPM < threshold)
		{
			leftSideFinished = true;
			AutoHardware._leftMotors.setControlTarget(0);
		}
		
		double rightSpeedRPM = AutoHardware._encRight.getSpeedInRPM();
		if(RobotMath.sgn(rightSpeedRPM) != rightSideDirection || rightSpeedRPM < threshold)
		{
			rightSideFinished = true;
			AutoHardware._rightMotors.setControlTarget(0);
		}
    }
    
    final static double threshold = 20; // RPM

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return timedOut || (leftSideFinished && rightSideFinished);
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
