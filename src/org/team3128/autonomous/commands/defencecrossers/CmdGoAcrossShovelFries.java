
 package org.team3128.autonomous.commands.defencecrossers;

import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLambda;
import org.team3128.autonomous.commands.CmdRunInParallel;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.misc.Piston;
import org.team3128.util.units.Length;

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
	public CmdGoAcrossShovelFries(TankDrive drive, Piston leftIntakePiston, Piston rightIntakePiston)
	{		 
		addSequential(drive.new CmdMoveForward(100*Length.cm,4000,false));
		addSequential(new CmdLambda(() -> {
			leftIntakePiston.setPistonOff();
			rightIntakePiston.setPistonOff();

		}));
		addSequential(new CmdDelay(1000));
		addSequential(new CmdRunInParallel(
				drive.new CmdMoveForward(200*Length.cm,5000,false), new CmdLambda(() -> {
			leftIntakePiston.setPistonOn();
			rightIntakePiston.setPistonOn();

		})));

	}
}


