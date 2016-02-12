package org.team3128.autonomous.commands;

import org.team3128.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.command.Command;

public class CmdToteGrab extends Command
{
	long time;
	long startTime;
	double speed;
	
	MotorGroup frontHookMotor;
	
	public CmdToteGrab(MotorGroup frontHookMotor, long time)
	{
		this.time = time;
		
		this.frontHookMotor = frontHookMotor;
	}
	
	@Override
	protected void initialize() {
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void execute() {
		frontHookMotor.setTarget(.3);
		
	}

	@Override
	protected boolean isFinished() {
		return (System.currentTimeMillis() - startTime) > time;
	}

	@Override
	protected void end() {
		frontHookMotor.setTarget(0);
		
	}

	@Override
	protected void interrupted() {
		frontHookMotor.setTarget(0);
		
	}
	

}
