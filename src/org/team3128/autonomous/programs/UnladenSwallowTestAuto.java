package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveUltrasonic;
import org.team3128.main.MainUnladenSwallow;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class UnladenSwallowTestAuto extends CommandGroup {
    
	public UnladenSwallowTestAuto(MainUnladenSwallow robot)
	{
		addSequential(new CmdMoveUltrasonic(robot.drive, robot.ultrasonic, 50, 2, 30000, .001, .000001));
    }
}
