package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveStraightForward;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class TestAuto extends CommandGroup {
    
	public TestAuto()
	{
		addSequential(new CmdMoveStraightForward(5*Units.m, 1, 10000));
    }
}
