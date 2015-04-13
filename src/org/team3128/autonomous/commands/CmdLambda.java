package org.team3128.autonomous.commands;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Bleah.  This command is an utter perversion of the Command system
 * But I need it for debugging, so there it is.
 * @author Narwhal
 *
 */
public class CmdLambda extends Command
{
	
	Runnable _toRun;
	
    public CmdLambda(Runnable toRun)
    {
    	_toRun = toRun;
    }

    @Override
    protected void initialize()
    {
    	_toRun.run();
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
