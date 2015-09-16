package org.team3128.autonomous.commands;

import org.team3128.Log;
import org.team3128.autonomous.AutoHardware;

import edu.wpi.first.wpilibj.command.Command;

public class CmdArmAngles extends Command
{
	double _shoulderAngle;
	double _elbowAngle;
	
	double _tolerance;
	
	double endTime;
	
	/**
	 * 
	 * @param shoulderAngle
	 * @param elbowAngle
	 * @param tolerance acceptible angle tolerance in degrees
	 * @param timeout milliseconds after which the move fili finish regardless of the arm's position
	 */
    public CmdArmAngles(double shoulderAngle, double elbowAngle, double tolerance, double timeout)
    {
    	_shoulderAngle = shoulderAngle;
    	_elbowAngle = elbowAngle;
    	
    	_tolerance = tolerance;
    	
    	endTime = System.currentTimeMillis() + timeout;
    }

    @Override
    protected void initialize()
    {
    	AutoHardware.clawArm.setArmAngle(_shoulderAngle);
    	AutoHardware.clawArm.setJointAngle(_elbowAngle);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//do nothing
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    
    	double shoulderError = Math.abs(AutoHardware.clawArm._armJointEncoder.getAngle() - _elbowAngle);
    	double elbowError = Math.abs(AutoHardware.clawArm._armRotateEncoder.getAngle() - _shoulderAngle);
    	
    	Log.debug("CmdArmAngles", "Shoulder error: " + shoulderError + " Elbow Error: " + elbowError);
    	return (elbowError < _tolerance && shoulderError < _tolerance) || System.currentTimeMillis() >= endTime;
    }

    // Called once after isFinished returns true
    protected void end()
    {
		
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
