package org.team3128.autonomous.commands;

import org.team3128.Log;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to log a message
 * @author Narwhal
 *
 */
public class CmdLog extends Command
{
	
	String _toLog;
    public CmdLog(String toLog)
    {
    	_toLog = toLog;
    }

    @Override
    protected void initialize()
    {
    	Log.info("CmdLog", _toLog);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	//do nothing
    }



    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {    	
    	return true;
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
