package org.team3128.autonomous.programs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.team3128.Log;
import org.team3128.autonomous.commands.defencecrossers.StrongholdStartingPosition;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.Direction;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto crosses defenses and scores based on the driver's input of the field data.
 */
public class StrongholdCompositeAuto extends CommandGroup {
    
	public StrongholdCompositeAuto(MainUnladenSwallow robot)
	{		
		CommandGroup defenseCrosser = robot.defenseChooser.getSelected();
		Class<? extends CommandGroup> scorerClass = robot.scoringChooser.getSelected();

		StrongholdStartingPosition startingPosition = robot.fieldPositionChooser.getSelected();
		
		if(defenseCrosser != null)
		{
			addSequential(defenseCrosser);
						
			if(scorerClass == null)
			{
				//make the robot fit for driving
				addSequential(robot.drive.new CmdInPlaceTurn(180, 2500, Direction.LEFT));
			}
			else
			{

				Constructor<? extends CommandGroup> ctor;
				try
				{
					
					ctor = scorerClass.getConstructor(MainUnladenSwallow.class, StrongholdStartingPosition.class);
					addSequential(ctor.newInstance(robot, startingPosition));

				}
				catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException| IllegalArgumentException| InvocationTargetException e1)
				{
					Log.recoverable("StrongholdCompositeAuto", "Could not construct second stage auto");
					e1.printStackTrace();
				}
				
			}
		}
		
	}
	
	
}
