package org.team3128.autonomous.programs;

import org.team3128.drive.TankDrive;
import org.team3128.hardware.lights.LightsColor;
import org.team3128.hardware.lights.PWMLights;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveIntoAutoZoneAuto extends CommandGroup
{
    public DriveIntoAutoZoneAuto(TankDrive tankDrive, PWMLights lights)
    {
    	addSequential(tankDrive.new CmdMoveForward(-200, 0));
    	lights.setColor(LightsColor.new4Bit(0xf, 0xf, 0xf));
    }
}