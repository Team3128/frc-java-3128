package org.team3128.autonomous.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *	This command is constructed with a group of commands.
 * They will be run in series when the command is invoked.
 */
public class CmdRunInSeries extends CommandGroup {
    
    public CmdRunInSeries(Command... commands)
    {
        if(commands == null || commands.length < 1)
        {
        	throw new IllegalArgumentException("You must provide at least one command!");
        }
        for(int index = 0; index < commands.length; ++index)
        {
        	addSequential(commands[index]);
        }
    }
}
