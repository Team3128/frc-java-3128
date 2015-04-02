package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdArcTurn;
import org.team3128.autonomous.commands.CmdArmAngles;
import org.team3128.autonomous.commands.CmdCloseClaw;
import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.autonomous.commands.CmdMoveForward;
import org.team3128.autonomous.commands.CmdOpenClaw;
import org.team3128.util.Direction;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DualFarCanGrabAuto extends CommandGroup
{
    public DualFarCanGrabAuto()
    {
    	addSequential(new CmdArmAngles(48, 280, 11, 5000));
    	addSequential(new CmdDelay(1500));
    	
    	addSequential(new CmdLog("Opening Claw And Positioning Arm"));
    	addParallel(new CmdOpenClaw(1400));
    	addSequential(new CmdArmAngles(50, 145, 10, 7500));
    	addSequential(new CmdDelay(4000));
    	
    	addSequential(new CmdLog("Driving To Can"));
    	addSequential(new CmdMoveForward(27, 2000));
    	
    	addSequential(new CmdLog("Grabbing Can 1"));
        addSequential(new CmdCloseClaw(2000));
    	addSequential(new CmdArmAngles(45, 230, 5, 2500));
    	addSequential(new CmdDelay(2000));
        
    	addSequential(new CmdLog("Backing Up"));
    	addSequential(new CmdMoveForward(-30, 10000));
    	
    	addSequential(new CmdLog("Opening Claw"));
        addParallel(new CmdOpenClaw(2000));
    	
    	addParallel(new CmdLog("Getting Into Position"));
    	addSequential(new CmdArcTurn(90, 0, Direction.LEFT));
    	addSequential(new CmdMoveForward(50, 10000));
    	addSequential(new CmdArcTurn(90, 0, Direction.RIGHT));
    	addSequential(new CmdMoveForward(30, 10000));
    	
    	addSequential(new CmdLog("Grabbing Can 2"));
        addSequential(new CmdCloseClaw(2000));
    	addSequential(new CmdArmAngles(45, 230, 5, 2500));

    }
}
