package org.team3128.autonomous.commands;

import edu.wpi.first.wpilibj.command.Command;

public class CmdDelay extends Command
{
	long startTime;
	
	long millisToWait;
	
    public CmdDelay(long millis)
    {
    	millisToWait = millis;
    }

    protected void initialize()
    {
    	startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//do nothing
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
    	return System.currentTimeMillis() > startTime + millisToWait;
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
