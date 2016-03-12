
 package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossShovelFries extends CommandGroup{
	public CmdGoAcrossShovelFries(MainUnladenSwallow robot)
	{		 
		addSequential(robot.drive.new CmdMoveForward(105*Length.cm,4000, .5));
		

		addSequential(robot.new CmdSetIntake(false));
		

		addSequential(robot.drive.new CmdMoveForward(-10*Length.cm, 1000, .4));

		addSequential(new CmdDelay(1000));
		addSequential(robot.drive.new CmdMoveForward(230*Length.cm, 5000,.6));

		

	}
}


