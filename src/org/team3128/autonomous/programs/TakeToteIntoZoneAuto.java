package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdToteGrab;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.Direction;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class TakeToteIntoZoneAuto extends CommandGroup
{
    public TakeToteIntoZoneAuto(TankDrive tankDrive, MotorGroup toteHookMotor)
    {
    	addSequential(tankDrive.new CmdMoveForward(5 * Units.cm, 1000));
    	addSequential(new CmdToteGrab(toteHookMotor, 1000));
    	addSequential(tankDrive.new CmdInPlaceTurn(90, 2000, Direction.RIGHT));
    	addSequential(tankDrive.new CmdMoveForward(200 * Units.cm, 0));
    }
}
