package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdCloseClaw;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.autonomous.commands.CmdMoveForward;
import org.team3128.autonomous.commands.CmdOpenClaw;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CloseCanGrabAuto extends CommandGroup
{
    public CloseCanGrabAuto()
    {
    	//addSequential(new CmdArmAngles(25, 160, 10, 7500));
    	addSequential(new CmdLog("Opening Claw"));
    	addSequential(new CmdOpenClaw());
    	
    	addSequential(new CmdLog("Driving To Can"));
    	addSequential(new CmdMoveForward(5, 2000));
    	
    	addSequential(new CmdLog("Closing Claw"));
        addSequential(new CmdCloseClaw(1500));
    	//addSequential(new CmdArmAngles(15, 180, 5, 2500));
        
    	addSequential(new CmdLog("Backing Up"));
    	addSequential(new CmdMoveForward(-20, 10000));

    }
}
