package org.team3128;

import java.util.ArrayList;

import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.programs.CanGrabAuto;
import org.team3128.autonomous.programs.TestMoveForwardAuto;
import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.angular.AnalogPotentiometerEncoder;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.mechanisms.ClawArm;
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
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	public AnalogPotentiometerEncoder armRotateEncoder;
	
	public AnalogPotentiometerEncoder armJointEncoder;
	
	public PowerDistributionPanel powerDistPanel;
	
	public ArcadeDrive _drive;
	
	public ClawArm clawArm;
	
	SendableChooser autoPrograms;
	
	public Global()
	{	
		_listenerManagerXbox = new ListenerManager(new Joystick(Options.instance()._controllerPort), ControllerXbox.instance);
		_listenerManagerJoyLeft = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);
		_listenerManagerJoyRight = new ListenerManager(new Joystick(2), ControllerAttackJoy.instance);
		
		_listenerManagers.add(_listenerManagerXbox);
		_listenerManagers.add(_listenerManagerJoyLeft);
		_listenerManagers.add(_listenerManagerJoyRight);
		
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
		
		armRotateEncoder = new AnalogPotentiometerEncoder(0);
		
		armJointMotor = new MotorLink();
		armJointMotor.addControlledMotor(new Talon(5));
		
		armJointEncoder = new AnalogPotentiometerEncoder(1);
		
		frontHookMotor = new MotorLink();
		frontHookMotor.addControlledMotor(new Talon(7));
		frontHookMotor.setSpeedController(new CurrentTarget(powerDistPanel, 3, .5));
		frontHookMotor.startControl(0);
		
		clawGrabMotor = new MotorLink();
		clawGrabMotor.addControlledMotor(new Talon(8));
		
		leftArmBrakeServo = new Servo(9);
		rightArmBrakeServo = new Servo(0);
		
		clawArm = new ClawArm(armTurnMotor, armJointMotor, clawGrabMotor, armRotateEncoder, armJointEncoder, powerDistPanel);

		_drive = new ArcadeDrive(leftMotors, rightMotors, _listenerManagerXbox);
		
		//--------------------------------------------------------------------------
		
		autoPrograms = new SendableChooser();
		autoPrograms.addDefault("Can Grab", new CanGrabAuto());
		autoPrograms.addObject("Test Move Forward", new TestMoveForwardAuto());
		
		SmartDashboard.putData("Autonomous Programs", autoPrograms);

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
		AutoHardware._encLeft = leftDriveEncoder;
		AutoHardware._encRight = rightDriveEncoder;
		
		AutoHardware._leftMotors = leftMotors;
		AutoHardware._rightMotors = rightMotors;
		
		Command autoCommand = (Command) autoPrograms.getSelected();
		
		autoCommand.start();
	}
	
	void initializeTeleop()
	{
		//-----------------------------------------------------------
		// Drive code, on Xbox controller
		//-----------------------------------------------------------
		IListenerCallback updateDrive = () -> _drive.steer();
		
		_listenerManagerXbox.addListener(ControllerXbox.JOY2X, updateDrive);
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
			clawArm.onArmRotateJoyInput(power);
		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = _listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			clawArm.onArmJointJoyInput(power);
		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN1, () -> 
		{
			frontHookMotor.startControl(-.15);
		});

		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> 
		{
			frontHookMotor.startControl(.15);
		});
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN2, () -> clawArm.switchArmToOtherSide());
		
	}
}
