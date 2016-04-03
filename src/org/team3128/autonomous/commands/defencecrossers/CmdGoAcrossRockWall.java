package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.main.MainUnladenSwallow;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossRockWall extends CommandGroup {
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
	 public CmdGoAcrossRockWall(MainUnladenSwallow robot)
	 {
		 addSequential(robot.gearshift.new CmdUpshift());
		 addSequential(robot.drive.new CmdMoveForward(700 * Length.cm, 5000, .9));
	 }
}
