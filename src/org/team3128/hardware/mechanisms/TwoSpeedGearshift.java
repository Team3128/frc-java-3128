package org.team3128.hardware.mechanisms;

import java.util.Arrays;
import java.util.List;

import org.team3128.hardware.misc.Piston;

import edu.wpi.first.wpilibj.command.Command;

public class TwoSpeedGearshift
{
	private List<Piston> shifters;
	
	private boolean inverted;
	
	private boolean inHighGear;
	
	/**
	 * 
	 * @param inverted Normally, low gear is with the pistons off and high gear is with them on.  If this parameter is true, than this is reversed.
	 * @param shifters THe pistons to use for shifting.
	 */
	public TwoSpeedGearshift(boolean inverted, Piston... shifters)
	{
		inHighGear = false;
		
		this.inverted = inverted;
		this.shifters = Arrays.<Piston>asList(shifters);
	}
	
	public void shiftToHigh()
	{
		for(Piston piston : shifters)
		{
			if(inverted)
			{
				piston.setPistonOff();
			}
			else
			{
				piston.setPistonOn();
			}
		}
		inHighGear = true;
	}
	
	public void shiftToLow()
	{
		for(Piston piston : shifters)
		{
			if(inverted)
			{
				piston.setPistonOn();
			}
			else
			{
				piston.setPistonOff();
			}
		}
		inHighGear = false;
	}
	
	public void shiftToOtherGear()
	{
		if(inHighGear)
		{
			shiftToLow();
		}
		else
		{
			shiftToHigh();
		}
	}
	
	public boolean isInHighGear()
	{
		return inHighGear;
	}

	public class CmdUpshift extends Command
	{

		@Override
		protected void initialize() {
			shiftToHigh();
			
		}

		@Override
		protected void execute() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected boolean isFinished() {
			return timeSinceInitialized() > .5;
		}

		@Override
		protected void end() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void interrupted() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class CmdDownshift extends Command
	{

		@Override
		protected void initialize() {
			shiftToLow();
			
		}

		@Override
		protected void execute() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected boolean isFinished() {
			return timeSinceInitialized() > .5;
		}

		@Override
		protected void end() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void interrupted() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
