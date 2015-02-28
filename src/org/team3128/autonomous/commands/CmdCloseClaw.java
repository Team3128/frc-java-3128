package org.team3128.autonomous.commands;

import org.team3128.autonomous.AutoHardware;

import edu.wpi.first.wpilibj.command.Command;

public class CmdCloseClaw extends Command
{
	long endTime;
	
	/**
	 * 
	 * @param timeout time to allow the claw to close
	 */
    public CmdCloseClaw(int timeout)
    {
    	endTime = System.currentTimeMillis() + timeout;
    }

    @Override
    protected void initialize()
    {
    	AutoHardware.clawArm.closeClaw();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//do nothing
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
    	return AutoHardware.clawArm.clawMinLimitSwitch.get() || endTime <= System.currentTimeMillis();
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	AutoHardware.clawArm._clawGrab.setControlTarget(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}
