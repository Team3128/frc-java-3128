package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdArmAngles;
import org.team3128.autonomous.commands.CmdCloseClaw;
import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.autonomous.commands.CmdMoveForward;
import org.team3128.autonomous.commands.CmdOpenClaw;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FarCanGrabAuto extends CommandGroup
{
    public FarCanGrabAuto()
    {
    	addSequential(new CmdArmAngles(45, 280, 10, 7500));
    	addSequential(new CmdDelay(2000));
    	
    	addSequential(new CmdLog("Opening Claw"));
    	addSequential(new CmdOpenClaw(1400));
    	addSequential(new CmdArmAngles(45, 130, 10, 7500));
    	addSequential(new CmdDelay(5000));
    	
    	addSequential(new CmdLog("Driving To Can"));
    	addSequential(new CmdMoveForward(27, 2000));
    	
    	addSequential(new CmdLog("Closing Claw"));
        addSequential(new CmdCloseClaw(2000));
    	addSequential(new CmdArmAngles(55, 220, 5, 2500));
    	addSequential(new CmdDelay(2000));
        
    	addSequential(new CmdLog("Backing Up"));
    	addSequential(new CmdMoveForward(-45, 10000));

    }
}
