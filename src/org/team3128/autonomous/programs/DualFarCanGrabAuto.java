package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.util.Direction;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DualFarCanGrabAuto extends CommandGroup
{
    public DualFarCanGrabAuto(TankDrive drive, ClawArm arm)
    {
    	addSequential(new CmdLog("Opening Claw And Positioning Arm"));
    	addParallel(arm.new CmdOpenClaw(1400));
    	addSequential(arm.new CmdArmAngles(50, 145, 10, 7500));
    	addSequential(new CmdDelay(3000));
    	
    	addSequential(new CmdLog("Driving To Can"));
    	addSequential(drive.new CmdMoveForward(27, 2000));
    	
    	addSequential(new CmdLog("Grabbing Can 1"));
        addSequential(arm.new CmdCloseClaw(2000));
    	addSequential(arm.new CmdArmAngles(45, 230, 5, 2500));
    	addSequential(new CmdDelay(2000));
        
    	addSequential(new CmdLog("Backing Up"));
    	addSequential(drive.new CmdMoveForward(-30, 10000));
    	
    	addSequential(new CmdLog("Opening Claw"));
        addParallel(arm.new CmdOpenClaw(2000));
    	
    	addParallel(new CmdLog("Getting Into Position"));
    	addSequential(drive.new CmdInPlaceTurn(90, 0, Direction.LEFT));
    	addSequential(drive.new CmdMoveForward(50, 10000));
    	addSequential(drive.new CmdInPlaceTurn(90, 0, Direction.RIGHT));
    	addSequential(arm.new CmdArmAngles(50, 145, 10, 7500));
    	addSequential(new CmdDelay(3000));
    	addSequential(drive.new CmdMoveForward(30, 10000));
    	
    	addSequential(new CmdLog("Grabbing Can 2"));
        addSequential(arm.new CmdCloseClaw(2000));
    	addSequential(arm.new CmdArmAngles(45, 230, 5, 2500));

    }
}
