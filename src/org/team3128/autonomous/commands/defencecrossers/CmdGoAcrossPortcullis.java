package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.units.Angle;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossPortcullis extends CommandGroup {

	 public CmdGoAcrossPortcullis(MainUnladenSwallow robot)
	 {
		 addSequential(robot.new CmdSetIntake(false));
		 addSequential(robot.backArm.new CmdMoveToAngle(3000, 200 * Angle.DEGREES));
		 
		 //addSequential(robot.drive.new CmdMoveStraightForward(-350 * Length.cm, MainUnladenSwallow.STRAIGHT_DRIVE_KP, 5000, .4));
		 addSequential(robot.drive.new CmdMoveForward(-350 * Length.cm, 5000, .7));

	 }
}