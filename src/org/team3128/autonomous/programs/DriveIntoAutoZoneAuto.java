package org.team3128.autonomous.programs;

import org.team3128.drive.TankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveIntoAutoZoneAuto extends CommandGroup
{
    public DriveIntoAutoZoneAuto(TankDrive tankDrive)
    {
    	addSequential(tankDrive.new CmdMoveForward(-200, 0));
    }
}
