package org.team3128;

import java.util.ArrayList;

import org.team3128.autonomous.AutoConfig;
import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.CurrentTarget;
import org.team3128.hardware.motor.speedcontrol.PIDSpeedTarget;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerAttackJoy;
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
	public ArrayList<ListenerManager> _listenerManagers = new ArrayList<ListenerManager>();
	public ListenerManager _listenerManagerXbox;
	public ListenerManager _listenerManagerJoyLeft;
	public ListenerManager _listenerManagerJoyRight;
	
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
		_listenerManagerXbox = new ListenerManager(new Joystick(Options.instance()._controllerPort), ControllerXbox.instance);
		_listenerManagerJoyLeft = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);
		_listenerManagerJoyRight = new ListenerManager(new Joystick(2), ControllerAttackJoy.instance);
		
		_listenerManagers.add(_listenerManagerXbox);
		
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
		clawGrabMotor.setSpeedController(new CurrentTarget(powerDistPanel, 15, .75));
		
		leftArmBrakeServo = new Servo(9);
		rightArmBrakeServo = new Servo(0);

		_drive = new ArcadeDrive(leftMotors, rightMotors, _listenerManagerXbox);

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
		//-----------------------------------------------------------
		// Drive code, on Xbox controller
		//-----------------------------------------------------------
		IListenerCallback updateDrive = () -> _drive.steer();
		
		_listenerManagerXbox.addListener(ControllerXbox.JOY1X, updateDrive);
		_listenerManagerXbox.addListener(ControllerXbox.JOY1Y, updateDrive);
		
		_listenerManagerXbox.addListener(ControllerXbox.R3DOWN, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
		//-----------------------------------------------------------
		// Arm control code, on joysticks
		//-----------------------------------------------------------
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = _listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYY);
			if(Math.abs(power) >= .1)
			{
				armJointMotor.setControlTarget(power);
			}
			else
			{
				armJointMotor.setControlTarget(0);
			}
		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = _listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			if(Math.abs(power) >= .1)
			{
				armTurnMotor.setControlTarget(power);
			}
			else
			{
				armTurnMotor.setControlTarget(0);
			}
		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYX, () ->
		{
			double leftPower = _listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			double rightPower = _listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYY);
			
			//if both joysticks are pushed in, close the claw
			if((leftPower > .7) && (rightPower < .7))
			{
				clawGrabMotor.startControl((leftPower - .7) * (10.0/3.0));
			}
			else if((leftPower < .7) && (rightPower > .7))
			{
				clawGrabMotor.startControl((leftPower - .7) * (10.0/3.0));
			}

		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN1, () -> 
		{
			frontHookMotor.startControl(-.15);
		});

		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> 
		{
			frontHookMotor.startControl(.15);
		});
		
	}
}
