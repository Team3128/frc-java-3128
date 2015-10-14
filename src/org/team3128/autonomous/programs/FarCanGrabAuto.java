package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdLog;
import org.team3128.autonomous.commands.CmdToteGrab;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FarCanGrabAuto extends CommandGroup
{
    public FarCanGrabAuto(TankDrive drive, ClawArm arm, MotorGroup frontHook, boolean grabTote)
    {
    	addSequential(arm.new CmdArmAngles(46, 280, 11, 5000));//81
    	addSequential(arm.new CmdOpenClaw(1400));
    	
    	addSequential(new CmdLog("Opening Claw"));
    	addSequential(arm.new CmdArmAngles(46, 145, 10, 7500));//81
    	addSequential(new CmdDelay(4000));
    	
    	addSequential(new CmdLog("Driving To Can"));
    	addSequential(drive.new CmdMoveForward(27, 2000));
    	
    	addSequential(new CmdLog("Closing Claw"));
        addSequential(arm.new CmdCloseClaw(2000));
        if(grabTote)
        {
        	addSequential(new CmdLog("Grabbing Tote"));
        	addSequential(new CmdToteGrab(frontHook, 0.25, 750));
        }
    	addSequential(arm.new CmdArmAngles(46, 230, 5, 2500));//85
    	addSequential(new CmdDelay(2000));
        
    	addSequential(new CmdLog("Backing Up"));
    	addSequential(drive.new CmdMoveForward(-200, 10000));

    }
}
