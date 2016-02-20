package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.drive.TankDrive;
import org.team3128.hardware.mechanisms.BackRaiserArm;
import org.team3128.util.units.Angle;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossPortcullis extends CommandGroup {
	/*
	    *        _
	    *       / \ 
	    *      / _ \
	    *     / | | \
	    *    /  |_|  \
	    *   /    _    \
	    *  /    (_)    \
	    * /_____________\
	    * -----------------------------------------------------
	    * UNTESTED CODE!
	    * This class has never been tried on an actual robot.
	    * It may be non or partially functional.
	    * Do not make any assumptions as to its behavior!
	    * And don't blink.  Not even for a second.
	    * -----------------------------------------------------*/
	 public CmdGoAcrossPortcullis(TankDrive drive, BackRaiserArm arm)
	 {
		 //addSequential(arm.new CmdMoveToAngle(5000, 220 * Angle.DEGREES));
		 addSequential(arm.new CmdMoveToAngle(5000, 175 * Angle.DEGREES));

		 addSequential(drive.new CmdMoveForward(-100 * Length.cm, 5000, false));
		 addSequential(arm.new CmdMoveToAngle(5000, 150 * Angle.DEGREES));
		 
		 addSequential(drive.new CmdMoveForward(-250 * Length.cm, 5000, false));


	 }
}
