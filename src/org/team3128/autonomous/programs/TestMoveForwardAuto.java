package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveForward;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class TestMoveForwardAuto extends CommandGroup {
    
    public  TestMoveForwardAuto() {
        addSequential(new CmdMoveForward(200, 0));
    }
}
