package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.autonomous.commands.CmdRunInSeries;

import edu.wpi.first.wpilibj.command.CommandGroup;
/**
 *	This auto is used by developers to run commands to test the robot
 */
public class TestAuto extends CommandGroup {
    
	public TestAuto()
	{
		addSequential(new CmdLog("Step 1"));
		addSequential(new CmdDelay(2000));
		addParallel(new CmdRunInSeries(new CmdLog("Step 2 Start"), new CmdDelay(3000), new CmdLog("Step 2 End")));
		addParallel(new CmdRunInSeries(new CmdLog("Step 3 Start"), new CmdDelay(5000), new CmdLog("Step 3 End")));
    }
}
