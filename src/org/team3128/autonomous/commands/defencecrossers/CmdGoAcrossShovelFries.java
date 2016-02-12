package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.drive.TankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdGoAcrossShovelFries extends CommandGroup{
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
	public CmdGoAcrossShovelFries(TankDrive drive)
	 {
		 addSequential(drive.new CmdMoveArm(5000, true, 100.0, drive));
	 }
}
