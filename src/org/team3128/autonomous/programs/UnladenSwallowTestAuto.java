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
		addSequential(new CmdMoveUltrasonic(robot.ultrasonic, robot.drive, 75, 10, new PIDConstants(.0016, .0006, .0018), 30000));
		//addSequential(robot.drive.new CmdMoveStraightForward(1 * Length.m, MainUnladenSwallow.STRAIGHT_DRIVE_KP, 5000, .4));
		//addSequential(robot.gearshift.new CmdDownshift());
		//addSequential(robot.drive.new CmdInPlaceTurn(180, .6, 7000, Direction.RIGHT));
		//addSequential(robot.gearshift.new CmdUpshift());
		//addSequential(robot.drive.new CmdMoveForward(1 * Length.m, 5000, .4));

    }
}
