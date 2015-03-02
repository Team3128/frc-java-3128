package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveForward;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveIntoAutoZoneAuto extends CommandGroup
{
    public DriveIntoAutoZoneAuto()
    {
    	addSequential(new CmdMoveForward(-200, 0));
    }
}
