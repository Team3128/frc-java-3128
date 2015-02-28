package org.team3128.autonomous.commands;

import org.team3128.autonomous.AutoHardware;

import edu.wpi.first.wpilibj.command.Command;

public class CmdOpenClaw extends Command
{
    public CmdOpenClaw()
    {
    }

    @Override
    protected void initialize()
    {
    	AutoHardware.clawArm.openClaw();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//do nothing
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
    	return AutoHardware.clawArm.clawMaxLimitSwitch.get();
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
