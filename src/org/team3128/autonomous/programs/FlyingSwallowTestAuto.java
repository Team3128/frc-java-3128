package org.team3128.autonomous.programs;

import org.team3128.main.MainUnladenSwallow;
import org.team3128.autonomous.commands.defencecrossers.CmdGoAcrossMoat;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class FlyingSwallowTestAuto extends CommandGroup {
    
	public FlyingSwallowTestAuto(MainUnladenSwallow robot)
	{
		//addSequential(robot.backArm.new CmdMoveToAngle(10000, 180 * Angle.DEGREES));
		
		addSequential(robot.drive.new CmdMoveForward(100 * Length.cm, 5000, false));
		addSequential(new CmdGoAcrossMoat(robot));
    }
}
