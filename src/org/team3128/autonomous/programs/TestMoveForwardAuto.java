package org.team3128.autonomous.programs;

import org.team3128.Log;
import org.team3128.autonomous.commands.CmdMoveForward;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class TestMoveForwardAuto extends CommandGroup {
    
    	Log.debug("TestMoveForwardAuto", "Auto started");
        addSequential(new CmdMoveForward(200, 0));
    }
}
