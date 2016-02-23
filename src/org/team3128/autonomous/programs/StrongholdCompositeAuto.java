package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.defencecrossers.IStrongholdPositionAccepter;
import org.team3128.autonomous.commands.defencecrossers.StrongholdStartingPosition;
import org.team3128.main.MainUnladenSwallow;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto crosses defenses and scores based on the driver's input of the field data.
 */
public class StrongholdCompositeAuto extends CommandGroup {
    
	MainUnladenSwallow robot;
	public StrongholdCompositeAuto(MainUnladenSwallow robot)
	{		
		this.robot = robot;
    }
	
	@Override
	protected void initialize()
	{
		CommandGroup defenseCrosser = robot.defenseChooser.getSelected();
		CommandGroup scorer = robot.scoringChooser.getSelected();

		StrongholdStartingPosition startingPosition = robot.fieldPositionChooser.getSelected();
		
		if(defenseCrosser != null)
		{
			addSequential(defenseCrosser);
			
			//defense crossers should end with the robot 50cm from the end of the defense
			
			if(scorer != null)
			{
				((IStrongholdPositionAccepter)scorer).setFieldPosition(startingPosition);
				addSequential(scorer);

			}
		}
		
	}
	
	
}
