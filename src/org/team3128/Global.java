package org.team3128;

import org.team3128.autonomous.AutoConfig;
import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.CurrentTarget;
import org.team3128.hardware.motor.speedcontrol.PIDSpeedTarget;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerXbox;
import org.team3128.util.VelocityPID;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;

/**
 * The Global class is where all of the hardware objects that represent the robot are stored.
 * It also sets up the control bindings.
 * 
 * Its functions are called only by RobotTemplate.
 * @author Jamie
 *
 */
public class Global
{
	public ListenerManager _listenerManager;
	
	public MotorLink _pidTestMotor;
	
	public MotorLink leftMotors;
	public MotorLink rightMotors;
	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	
	public MotorLink armTurnMotor;
	
	public MotorLink armJointMotor;
	
	Servo leftArmBrakeServo;
	Servo rightArmBrakeServo;
	
	public MotorLink frontHookMotor;
	
	public MotorLink clawGrabMotor;
	
	public PowerDistributionPanel powerDistPanel;
	
	public ArcadeDrive _drive;
	
	public Global()
	{	
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort), ControllerXbox.instance);
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128);
		rightDriveEncoder = new QuadratureEncoderLink(3, 4, 128);
		
		leftMotors = new MotorLink(new PIDSpeedTarget(0, leftDriveEncoder, new VelocityPID(.1, 0, 0)));
		leftMotors.addControlledMotor(new Talon(1));
		leftMotors.addControlledMotor(new Talon(2));
		leftMotors.reverseMotor();
		leftMotors.startControl(0);
		
		
		rightMotors = new MotorLink(new PIDSpeedTarget(0, rightDriveEncoder, new VelocityPID(.1, 0, 0)));
		rightMotors.addControlledMotor(new Talon(3));
		rightMotors.addControlledMotor(new Talon(4));
		rightMotors.startControl(0);
		
		armTurnMotor = new MotorLink();
		armTurnMotor.addControlledMotor(new Talon(6));
		
		armJointMotor = new MotorLink();
		armJointMotor.addControlledMotor(new Talon(5));
		
		frontHookMotor = new MotorLink();
		frontHookMotor.addControlledMotor(new Talon(7));
		frontHookMotor.setSpeedController(new CurrentTarget(powerDistPanel, 3, .5));
		frontHookMotor.startControl(0);
		
		clawGrabMotor = new MotorLink();
		clawGrabMotor.addControlledMotor(new Talon(8));
		
		leftArmBrakeServo = new Servo(9);
		rightArmBrakeServo = new Servo(0);

		_drive = new ArcadeDrive(leftMotors, rightMotors, _listenerManager);

	}

	void initializeRobot()
	{
		
//		TachLink link = new TachLink(0, 54);
//		
//		Log.debug("Global", "Tachometer: " + link.getRaw());
	}

	void initializeDisabled()
	{
		leftMotors.clearSpeedControlRun();
		rightMotors.clearSpeedControlRun();
	}

	void initializeAuto()
	{
		new Thread(() -> AutoConfig.initialize(this), "AutoConfig").start();
	}
	
	void initializeTeleop()
	{
		IListenerCallback updateDrive = () -> _drive.steer();
		
		_listenerManager.addListener(ControllerXbox.JOY1X, updateDrive);
		_listenerManager.addListener(ControllerXbox.JOY1Y, updateDrive);
		
		_listenerManager.addListener(ControllerXbox.JOY2X, () ->
		{
			double power = _listenerManager.getRawAxis(ControllerXbox.JOY2X);
			if(Math.abs(power) >= .1)
			{
				armJointMotor.setControlTarget(power);
			}
			else
			{
				armJointMotor.setControlTarget(0);
			}
		});
		
		_listenerManager.addListener(ControllerXbox.JOY2Y, () ->
		{
			double power = _listenerManager.getRawAxis(ControllerXbox.JOY2Y);
			if(Math.abs(power) >= .1)
			{
				armTurnMotor.setControlTarget(power);
			}
			else
			{
				armTurnMotor.setControlTarget(0);
			}
		});
		
		_listenerManager.addListener(ControllerXbox.ADOWN, () -> 
		{
			frontHookMotor.startControl(-.15);
		});

		_listenerManager.addListener(ControllerXbox.BDOWN, () -> 
		{
			frontHookMotor.startControl(.15);
		});
		
		_listenerManager.addListener(ControllerXbox.R3DOWN, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
	}
}
