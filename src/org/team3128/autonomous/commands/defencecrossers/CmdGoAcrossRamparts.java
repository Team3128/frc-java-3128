package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.autonomous.commands.CmdRunInParallel;
import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossRamparts extends CommandGroup {
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
	 public CmdGoAcrossRamparts(MainUnladenSwallow robot)
	 {
		 addSequential(new CmdRunInParallel(robot.new CmdSetIntake(false), robot.gearshift.new CmdDownshift()));
		 addSequential(robot.drive.new CmdMoveForward(200 * Length.cm, 6000, .3));
	 }
}
