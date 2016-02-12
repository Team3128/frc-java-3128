package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.drive.TankDrive;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossMoat extends CommandGroup {
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
	 public CmdGoAcrossMoat(TankDrive drive)
	 {
		 addSequential(drive.new CmdMoveForward(500 * Units.cm, 5000, true));
	 }
}
