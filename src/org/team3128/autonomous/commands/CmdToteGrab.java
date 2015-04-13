package org.team3128.autonomous.commands;

import org.team3128.autonomous.AutoHardware;

import edu.wpi.first.wpilibj.command.Command;

public class CmdToteGrab extends Command{
	long timeout;
	long startTime;
	double speed;
	public CmdToteGrab(double spd, long timeout){
		this.timeout = timeout;
		speed = spd;
	}
	@Override
	protected void initialize() {
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void execute() {
		AutoHardware._frontHookMotor.setControlTarget(speed);
		
	}

	@Override
	protected boolean isFinished() {
		return (System.currentTimeMillis() - startTime) > timeout;
	}

	@Override
	protected void end() {
		AutoHardware._frontHookMotor.setControlTarget(0);
		
	}

	@Override
	protected void interrupted() {
		AutoHardware._frontHookMotor.setControlTarget(0);
		
	}
	

}
