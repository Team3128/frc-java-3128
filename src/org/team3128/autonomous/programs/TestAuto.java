package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdMoveUltrasonic;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.PIDConstants;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class TestAuto extends CommandGroup {
    
	public TestAuto(MainUnladenSwallow robot)
	{
		PIDConstants ultrasonicPIDConstants = new PIDConstants(.000001, 0, 0);

		 addSequential(new CmdMoveUltrasonic(robot.frontUltrasonic, robot.drive, 70 * Length.cm, 5 * Length.cm, ultrasonicPIDConstants, 7000));
    }
}
