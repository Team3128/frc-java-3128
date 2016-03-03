package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.defencecrossers.StrongholdStartingPosition;
import org.team3128.autonomous.commands.scorers.CmdScoreEncoders;
import org.team3128.main.MainUnladenSwallow;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto crosses defenses and scores based on the driver's input of the field data.
 */
public class StrongholdCompositeAuto extends CommandGroup {
    
	public StrongholdCompositeAuto(MainUnladenSwallow robot)
	{		
		CommandGroup defenseCrosser = robot.defenseChooser.getSelected();
		CommandGroup scorer = robot.scoringChooser.getSelected();

		StrongholdStartingPosition startingPosition = robot.fieldPositionChooser.getSelected();
		
		if(defenseCrosser != null)
		{
			addSequential(defenseCrosser);
			
			
			if(scorer != null)
			{
				addSequential(new CmdScoreEncoders(robot, startingPosition));
				

			}
		}
		
	}
	
	
}
