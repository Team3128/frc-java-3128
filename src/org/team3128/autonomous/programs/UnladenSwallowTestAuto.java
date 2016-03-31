package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveUltrasonic;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.PIDConstants;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class UnladenSwallowTestAuto extends CommandGroup {
    
	public UnladenSwallowTestAuto(MainUnladenSwallow robot)
	{
		addSequential(new CmdMoveUltrasonic(robot.ultrasonic, robot.drive, 50, 2, new PIDConstants(.0001, .000001, 0), 30000));
    }
}
