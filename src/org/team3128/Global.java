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
import org.team3128.hardware.motor.speedcontrol.LimitSwitchEndstop;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerAttackJoy;
import org.team3128.listener.controller.ControllerExtreme3D;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;

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
	public ListenerManager _listenerManagerExtreme;
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
	
	public DigitalInput clawMinLimitSwitch;
	public DigitalInput clawMaxLimitSwitch;
	
	public AnalogPotentiometerEncoder armRotateEncoder;
	
	public AnalogPotentiometerEncoder armJointEncoder;
	
	public PowerDistributionPanel powerDistPanel;
	
	public ArcadeDrive _drive;
	
	public ClawArm clawArm;
	
	public AxisCamera camera;
	
	SendableChooser autoPrograms;
	
	IListenerCallback updateDriveArcade;
	IListenerCallback updateDriveCOD;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	public Global()
	{	
		_listenerManagerExtreme = new ListenerManager(new Joystick(Options.instance()._controllerPort), ControllerExtreme3D.instance);
		_listenerManagerJoyLeft = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);
		_listenerManagerJoyRight = new ListenerManager(new Joystick(2), ControllerAttackJoy.instance);
		
		_listenerManagers.add(_listenerManagerExtreme);
		_listenerManagers.add(_listenerManagerJoyLeft);
		_listenerManagers.add(_listenerManagerJoyRight);
		
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(3, 4, 128, true);
		
		leftMotors = new MotorLink(/*new PIDSpeedTarget(0, leftDriveEncoder, new VelocityPID(.1, 0, 0))*/);
		leftMotors.addControlledMotor(new Talon(1));
		leftMotors.addControlledMotor(new Talon(2));
		//leftMotors.startControl(0);
		
		
		rightMotors = new MotorLink(/*new PIDSpeedTarget(0, rightDriveEncoder, new VelocityPID(.1, 0, 0))*/);
		rightMotors.addControlledMotor(new Talon(3));
		rightMotors.addControlledMotor(new Talon(4));
		rightMotors.reverseMotor();
		//rightMotors.startControl(0);
		
		armTurnMotor = new MotorLink();
		armTurnMotor.addControlledMotor(new Talon(6));
		
		armRotateEncoder = new AnalogPotentiometerEncoder(0, 0, 4.829, 300);
		
		armJointMotor = new MotorLink();
		armJointMotor.addControlledMotor(new Talon(5));
		
		armJointEncoder = new AnalogPotentiometerEncoder(1, 0, 4.829, 300);
		
		frontHookMotor = new MotorLink();
		frontHookMotor.addControlledMotor(new Talon(7));
		
		clawMinLimitSwitch = new DigitalInput(9);
		clawMaxLimitSwitch = new DigitalInput(8);
		clawGrabMotor = new MotorLink(new LimitSwitchEndstop(clawMinLimitSwitch, clawMaxLimitSwitch, false));
		clawGrabMotor.addControlledMotor(new Talon(8));
		clawGrabMotor.reverseMotor();
		clawGrabMotor.startControl(0);
		
		leftArmBrakeServo = new Servo(9);
		rightArmBrakeServo = new Servo(0);
		
		//camera = new AxisCamera("192.168.1.196");
		clawArm = new ClawArm(armTurnMotor, armJointMotor, clawGrabMotor, armRotateEncoder, armJointEncoder, powerDistPanel);

		_drive = new ArcadeDrive(leftMotors, rightMotors, _listenerManagerExtreme);
		
		updateDriveArcade = () ->
		{
			double joyX = _listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYX);
			double joyY = _listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -_listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			_drive.steer(joyX, joyY, throttle);
		};
		
		updateDriveCOD = () ->
		{
			double joyX = _listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = _listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -_listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			/*if (Math.abs(_listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST)) < 0.1)
			{
				joyX = 0;
			}*/
			
			_drive.steer(joyX, joyY, throttle);
		};
		
		//--------------------------------------------------------------------------
		// Auto Init
		//--------------------------------------------------------------------------

		AutoHardware._encLeft = leftDriveEncoder;
		AutoHardware._encRight = rightDriveEncoder;
		
		AutoHardware._leftMotors = leftMotors;
		AutoHardware._rightMotors = rightMotors;
		
		
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
		clawArm.stopClawLimitThread();
		
		leftMotors.clearSpeedControlRun();
		rightMotors.clearSpeedControlRun();
	}

	void initializeAuto()
	{
		Command autoCommand = (Command) autoPrograms.getSelected();
		
		autoCommand.start();
	}
	
	void initializeTeleop()
	{
		//-----------------------------------------------------------
		// Drive code, on Logitech Extreme3D joystick
		//-----------------------------------------------------------
		_listenerManagerExtreme.addListener(ControllerExtreme3D.TWIST, updateDriveCOD);
		_listenerManagerExtreme.addListener(ControllerExtreme3D.JOYY, updateDriveCOD);
		_listenerManagerExtreme.addListener(ControllerExtreme3D.THROTTLE, updateDriveCOD);
		
		
		_listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN12, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
				
		//_listenerManagerExtreme.addListener(Always.instance, () -> System.out.println(armJointEncoder.getAngle()));
		
		//-----------------------------------------------------------
		// Arm control code, on joysticks
		//-----------------------------------------------------------
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = _listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYY);
			clawArm.onArmJoyInput((shoulderInverted ? 1 : -1) * power);
		});
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = _listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			clawArm.onJointJoyInput((elbowInverted ? 1 : -1) * power);
		});
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN2, () -> shoulderInverted = false);
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN3, () -> shoulderInverted = true);
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN6, () -> shoulderInverted = true);
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN7, () -> shoulderInverted = false);
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN2, () -> elbowInverted = false);
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN3, () -> elbowInverted = true);
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN6, () -> elbowInverted = true);
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN7, () -> elbowInverted = false);
		
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(0.7));
		_listenerManagerJoyLeft.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(-0.7));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN4, () -> frontHookMotor.setControlTarget(0.3));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.UP4, () -> frontHookMotor.setControlTarget(0));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN5, () -> frontHookMotor.setControlTarget(-0.3));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.UP5, () -> frontHookMotor.setControlTarget(0));

		_listenerManagerExtreme.addListener(ControllerExtreme3D.UP8, () -> frontHookMotor.startControl(0));
		
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN10, () -> clawArm.setArmAngle(150));
		_listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN11, () -> clawArm.setArmAngle(200));
		
		clawArm.startClawLimitThread();
	}
}
