package org.team3128.autonomous;

import org.team3128.autonomous.programs.DoNothingAuto;
import org.team3128.autonomous.programs.DriveIntoAutoZoneAuto;
import org.team3128.autonomous.programs.DualFarCanGrabAuto;
import org.team3128.autonomous.programs.FarCanGrabAuto;
import org.team3128.autonomous.programs.TestAuto;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is a simple auto chooser that uses the Smart Dashboard.
 * It adds a simple pulldown menu with all of the choices.
 * @author Jamie
 *
 */
public class AutoChooser
{
	SendableChooser autoPrograms;
	
	/**
	 * Construct an AutoChooser and put the pulldown on the dashboard.
	 * 
	 * Edit this if you want to add a new auto program.
	 */
	public AutoChooser()
	{
		autoPrograms = new SendableChooser();
		autoPrograms.addDefault("Far Can Grab", new FarCanGrabAuto());
		autoPrograms.addObject("DualFar Can Grab", new DualFarCanGrabAuto());
		autoPrograms.addObject("Drive Into Auto Zone", new DriveIntoAutoZoneAuto());
		autoPrograms.addObject("Do Nothing", new DoNothingAuto());
		autoPrograms.addObject("Dev Test Auto", new TestAuto());
		
		SmartDashboard.putData("Autonomous Programs", autoPrograms);
	}
	
	/**
	 * Get the command group that was selected.
	 * @return
	 */
	public CommandGroup getChosen()
	{
		return (CommandGroup) autoPrograms.getSelected();
	}
}
