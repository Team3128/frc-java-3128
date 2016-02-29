
 package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossShovelFries extends CommandGroup{
	public CmdGoAcrossShovelFries(MainUnladenSwallow robot)
	{		 
		addSequential(robot.drive.new CmdMoveForward(105*Length.cm,4000,false));
		addSequential(robot.new CmdSetIntake(false));
		
		addSequential(robot.drive.new CmdMoveForward(102*Length.cm,4000,false));

		addSequential(robot.drive.new CmdMoveForward(210*Length.cm,5000,.4));

	}
}


