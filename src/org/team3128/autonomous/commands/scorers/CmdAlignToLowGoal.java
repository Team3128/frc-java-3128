package org.team3128.autonomous.commands.scorers;

import org.team3128.Log;
import org.team3128.main.MainUnladenSwallow;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.TableKeyNotDefinedException;

public class CmdAlignToLowGoal extends Command
{
	final static double Kp = .0001;
	final static double power = .5;
	final static double accuracyThresholdPx = 20;  //how close the actual needs to be to the target before victory can be declared.
	
	NetworkTable dataTable;
	
	MainUnladenSwallow robot;
	
	double desiredGoalX;
	
	double[] area;
	double[] centerx;
	double[] centery;
	double[] solidity;
	
	boolean isDone = false;
	//int noDataRetryCounter = 20; //ticks
	
	public CmdAlignToLowGoal(MainUnladenSwallow robot, double desiredHorizontalPosition)
	{
		this.robot = robot;
		desiredGoalX = desiredHorizontalPosition;
	}
	
	@Override
	protected void initialize()
	{
		dataTable = NetworkTable.getTable("GRIP/LowGoalContours");
	}

	@Override
	protected void execute()
	{
		//wait for NetworkTables to refresh (100 ms update time)
		try
		{
			Thread.sleep(100);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		updateContourData();
		
		if(area == null || area.length == 0)
		{

			Log.unusual("CmdAlignToLowGoal", "Can't see anything resembling the goal.  ");
			
			Log.info("CmdAlignToLowGoal", "Are you still there?");

			//Spin around.
			robot.drive.tankDrive(-1 * power, power);

			//this may happen in actual competition.  In that case, we fall back to dead reckoning.
			//if(noDataRetryCounter < 1)
			//{
			//	isDone = true;
			//}
			//else
			//{
			////	--noDataRetryCounter;
			//}
			

		}
		else
		{

			int preferredContourIdx = getMostLikelyContourIdx();
			
			//actually do some vision.
			double offsetPx = desiredGoalX - centerx[preferredContourIdx];
			Log.info("CmdAlignToLowGoal", "There you are. (error: " + offsetPx + "px)");

			
			//if offset is negative, the goal is too far to the right and we need to turn left
			//and vise versa
			
			robot.drive.arcadeDrive(0, power * Kp * offsetPx, 1, true);
			

			if(offsetPx < accuracyThresholdPx)
			{
				Log.info("CmdAlignToLowGoal", "Deploying.");
				isDone = true;

			}
		}
		

	}

	@Override
	protected boolean isFinished()
	{
		return isDone;
	}

	@Override
	protected void end()
	{
		robot.drive.stopMovement();
	}

	@Override
	protected void interrupted()
	{
		end();
	}
	
	int getMostLikelyContourIdx()
	{
		//the goal often shows up as multiple contours close to each other
		
		int numContours = area.length;
		
		if(numContours == 0)
		{
			//easy;
			return 0;
		}
		
		//how close two contours have to be to be considered parts of the goal
		final int closenessThreshold = 50; //px
		
		for(int contour1Counter = 0; contour1Counter < numContours; ++contour1Counter)
		{
			for(int contour2Counter = 0; contour2Counter < numContours; ++contour2Counter)
			{
				if(
						Math.abs(centerx[contour1Counter] - centerx[contour2Counter]) < closenessThreshold
						&& Math.abs(centery[contour1Counter] - centery[contour2Counter]) < closenessThreshold)
				{
					//Found them!
					return contour1Counter;
				}
			}
		}
		
		//when in doubt, go with the largest area
		int largestAreaIndex = 0;
		
		for(int contourIndex = 1; contourIndex < numContours; ++contourIndex) //oddly enough, there's no library function to find the largest value in an array
		{
			if(area[contourIndex] > area[largestAreaIndex])
			{
				largestAreaIndex = contourIndex;
			}
		}
		
		return largestAreaIndex;
	}
	
	@SuppressWarnings("deprecation")
	void updateContourData()
	{
		try
		{
			area = dataTable.getNumberArray("area"); 
			centerx = dataTable.getNumberArray("centerX");
			centery = dataTable.getNumberArray("centerY");
			solidity = dataTable.getNumberArray("solidity");
		}
		catch(TableKeyNotDefinedException ex)
		{
			Log.recoverable("CmdAlignToLowGoal", "Could not get any vision data from NetworkTables!  Is GRIP running? Is the publish address right?");

			Log.info("CmdAlignToLowGoal", "Target lost.");

			//fail semi-gracefully
			isDone = true;
		}
	}

}
