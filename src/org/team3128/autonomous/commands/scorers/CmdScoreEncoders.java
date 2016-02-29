package org.team3128.autonomous.commands.scorers;

import org.team3128.autonomous.commands.CmdLambda;
import org.team3128.autonomous.commands.CmdRunInParallel;
import org.team3128.autonomous.commands.defencecrossers.StrongholdStartingPosition;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.Direction;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * try to score the boulder in auto using encoder-based movement
 * @author Jamie
 *
 */
public class CmdScoreEncoders extends CommandGroup
{
	MainUnladenSwallow robot;

	public CmdScoreEncoders(MainUnladenSwallow robot, StrongholdStartingPosition startingPosition)
	{
		this.robot = robot;
		
		addSequential(new CmdRunInParallel(robot.new CmdSetIntake(false), robot.gearshift.new CmdDownshift()));
		 
		 switch(startingPosition)
		 {
		 case FAR_LEFT:
			 addSequential(robot.drive.new CmdMoveForward(7 * Length.ft, 5000, .45));
			 addSequential(robot.drive.new CmdInPlaceTurn(45, 2000, Direction.RIGHT));
			 addSequential(robot.drive.new CmdMoveForward(10 * Length.ft, 2000, .45));
			 break;
		 case CENTER_LEFT:
			 addSequential(robot.drive.new CmdMoveForward(250 * Length.cm, 5000, .3));
			 addSequential(robot.drive.new CmdInPlaceTurn(45, 2000, Direction.RIGHT));
			 addSequential(robot.drive.new CmdMoveForward(75 * Length.cm, 2000, .4));
			 break;
		 case MIDDLE:
			 addSequential(robot.drive.new CmdInPlaceTurn(30, 2000, Direction.LEFT));
			 addSequential(robot.drive.new CmdMoveForward(300 * Length.cm, 5000, .3));
			 addSequential(robot.drive.new CmdInPlaceTurn(75, 3000, Direction.RIGHT));
			 addSequential(robot.drive.new CmdMoveForward(100 * Length.cm, 2000, .4));
			 break;
		 case CENTER_RIGHT:
			 addSequential(robot.drive.new CmdInPlaceTurn(30, 2000, Direction.RIGHT));
			 addSequential(robot.drive.new CmdMoveForward(350 * Length.cm, 5000, .3));
			 addSequential(robot.drive.new CmdInPlaceTurn(75, 3000, Direction.LEFT));
			 addSequential(robot.drive.new CmdMoveForward(100 * Length.cm, 2000, .4));
			 break;
		 case FAR_RIGHT:
			 addSequential(robot.drive.new CmdMoveForward(300 * Length.cm, 5000, .3));
			 addSequential(robot.drive.new CmdInPlaceTurn(45, 2000, Direction.RIGHT));
			 addSequential(robot.drive.new CmdMoveForward(50 * Length.cm, 2000, .4));
			 break;
		 }
		 
		 addSequential(new CmdLambda(() -> {
			 robot.innerRoller.setTarget(-1);
			 robot.intakeSpinner.setTarget(MainUnladenSwallow.IntakeState.OUTTAKE.motorPower);
			 
			 try {
				Thread.sleep(2000);
			} 
			 catch (Exception e)
			 {
				e.printStackTrace();
			}
			 
			 robot.innerRoller.setTarget(0);
			 robot.intakeSpinner.setTarget(MainUnladenSwallow.IntakeState.STOPPED.motorPower);
		 }));
	}


}
