package org.team3128.autonomous.programs;

import org.team3128.Log;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CanGrabAuto extends CommandGroup
{
    public CanGrabAuto()
    {
    	Log.debug("CanGrabAuto", "Auto started");
        //addSequential(new CmdMoveForward(200, 0));
    }
}
