package org.team3128.autonomous.programs;

import org.team3128.autonomous.commands.CmdArcTurn;
import org.team3128.autonomous.commands.CmdBrake;
import org.team3128.autonomous.commands.CmdDelay;
import org.team3128.autonomous.commands.CmdInPlaceTurn;
import org.team3128.autonomous.commands.CmdMoveForward;
import org.team3128.util.Direction;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CanGrabAuto extends CommandGroup
{
    public CanGrabAuto()
    {
        addSequential(new CmdMoveForward(2 * Units.M, 0));
        addSequential(new CmdDelay(5000));
        addSequential(new CmdBrake(.25, 0));
        addSequential(new CmdArcTurn(90, 0, Direction.RIGHT));
        addSequential(new CmdInPlaceTurn(90, 0, Direction.RIGHT));
    }
}
