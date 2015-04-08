package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.Options;
import org.team3128.RobotTemplate;
import org.team3128.autonomous.AutoHardware;
import org.team3128.autonomous.programs.DoNothingAuto;
import org.team3128.autonomous.programs.DriveIntoAutoZoneAuto;
import org.team3128.autonomous.programs.DualFarCanGrabAuto;
import org.team3128.autonomous.programs.FarCanGrabAuto;
import org.team3128.autonomous.programs.TestAuto;
import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.angular.AnalogPotentiometerEncoder;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.control.Always;
import org.team3128.listener.controller.ControllerAttackJoy;
import org.team3128.listener.controller.ControllerExtreme3D;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.vision.AxisCamera;

/**
 * Main class for our 2015 robot, The Clawwww.
 * @author Jamie
 *
 */
public class MainTheClawwww extends MainClass
{
	public ListenerManager listenerManagerExtreme;
	public ListenerManager listenerManagerJoyLeft;
	public ListenerManager listenerManagerJoyRight;
	
	public MotorGroup _pidTestMotor;
	
	public MotorGroup leftMotors;
	public MotorGroup rightMotors;
	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	
	public MotorGroup armTurnMotor;
	
	public MotorGroup armJointMotor;
	
	Servo leftArmBrakeServo;
	Servo rightArmBrakeServo;
	
	public MotorGroup frontHookMotor;
	
	public MotorGroup clawGrabMotor;

	public AnalogPotentiometerEncoder armRotateEncoder;
	
	public AnalogPotentiometerEncoder armJointEncoder;
	
	public PowerDistributionPanel powerDistPanel;
	
	public ArcadeDrive drive;
	
	public ClawArm clawArm;
	
	public AxisCamera camera;
	
	IListenerCallback updateDriveArcade;
	IListenerCallback updateDriveCOD;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	public MainTheClawwww()
	{	
		listenerManagerExtreme = new ListenerManager(new Joystick(Options.controllerPort), ControllerExtreme3D.instance);
		listenerManagerJoyLeft = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);
		listenerManagerJoyRight = new ListenerManager(new Joystick(2), ControllerAttackJoy.instance);		
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(3, 4, 128, true);
		
		leftMotors = new MotorGroup(/*new PIDSpeedTarget(0, leftDriveEncoder, new VelocityPID(.1, 0, 0))*/);
		leftMotors.addControlledMotor(new Talon(1));
		leftMotors.addControlledMotor(new Talon(2));
		//leftMotors.startControl(0);
		
		
		rightMotors = new MotorGroup(/*new PIDSpeedTarget(0, rightDriveEncoder, new VelocityPID(.1, 0, 0))*/);
		rightMotors.addControlledMotor(new Talon(3));
		rightMotors.addControlledMotor(new Talon(4));
		rightMotors.reverseMotor();
		//rightMotors.startControl(0);
		
		armTurnMotor = new MotorGroup();
		armTurnMotor.addControlledMotor(new Talon(6));
		
		armRotateEncoder = new AnalogPotentiometerEncoder(0, 0, 4.829, 300);
		
		armJointMotor = new MotorGroup();
		armJointMotor.addControlledMotor(new Talon(5));
		
		armJointEncoder = new AnalogPotentiometerEncoder(1, 0, 4.829, 300);
		
		frontHookMotor = new MotorGroup();
		frontHookMotor.addControlledMotor(new Talon(7));
		
		clawGrabMotor = new MotorGroup();
		clawGrabMotor.addControlledMotor(new Talon(8));
		
		leftArmBrakeServo = new Servo(9);
		rightArmBrakeServo = new Servo(0);
		
		//camera = new AxisCamera("192.168.1.196");
		clawArm = new ClawArm(armTurnMotor, armJointMotor, clawGrabMotor, armRotateEncoder, armJointEncoder, powerDistPanel);

		drive = new ArcadeDrive(leftMotors, rightMotors, listenerManagerExtreme);
		
		updateDriveCOD = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			drive.steer(joyX, joyY, throttle, listenerManagerExtreme.getRawBool(ControllerExtreme3D.DOWN2));
		};
		
		//--------------------------------------------------------------------------
		// Auto Init
		//--------------------------------------------------------------------------

		AutoHardware.encLeft = leftDriveEncoder;
		AutoHardware.encRight = rightDriveEncoder;
		
		AutoHardware.leftMotors = leftMotors;
		AutoHardware.rightMotors = rightMotors;
		
		AutoHardware.clawArm = clawArm;
		

	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		robotTemplate.addListenerManager(listenerManagerJoyLeft);
		robotTemplate.addListenerManager(listenerManagerJoyRight);
	}

	protected void initializeDisabled()
	{
		clawArm.resetTargets();
		
		armTurnMotor.clearSpeedControlRun();
		armJointMotor.clearSpeedControlRun();
		clawArm.switchJointToManualControl();
		
		clawArm.stopClawLimitThread();
				
		leftMotors.clearSpeedControlRun();
		rightMotors.clearSpeedControlRun();
	}

	protected void initializeAuto()
	{
		clawArm.resetTargets();
	}
	
	protected void initializeTeleop()
	{	
		//-----------------------------------------------------------
		// Drive code, on Logitech Extreme3D joystick
		//-----------------------------------------------------------
		listenerManagerExtreme.addListener(ControllerExtreme3D.TWIST, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.JOYY, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.THROTTLE, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN2, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP2, updateDriveCOD);

		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN12, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
				
		listenerManagerExtreme.addListener(Always.instance, () -> System.out.println(armJointEncoder.getAngle()));
		
		//-----------------------------------------------------------
		// Arm control code, on joysticks
		//-----------------------------------------------------------
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = (shoulderInverted ? Options.armSpeedMultiplier : -Options.armSpeedMultiplier) * listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYY);
			
			if(power < 0)
			{
				power /= 1.5;
			}
			
			clawArm.onArmJoyInput(power);
			
		});
		
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			clawArm.onJointJoyInput((elbowInverted ? Options.armSpeedMultiplier : -Options.armSpeedMultiplier) * power);
		});
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN2, () -> shoulderInverted = false);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN3, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN6, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN7, () -> shoulderInverted = false);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN2, () -> elbowInverted = false);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN3, () -> elbowInverted = true);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN6, () -> elbowInverted = true);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN7, () -> elbowInverted = false);
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(0.7));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(-0.7));
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN4, () -> frontHookMotor.setControlTarget(0.3));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP4, () -> frontHookMotor.setControlTarget(0));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN5, () -> frontHookMotor.setControlTarget(-0.3));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP5, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN3, () -> frontHookMotor.setControlTarget(0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP3, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN4, () -> frontHookMotor.setControlTarget(-0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP4, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN5, () -> frontHookMotor.setControlTarget(0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP5, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN6, () -> frontHookMotor.setControlTarget(-0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP6, () -> frontHookMotor.setControlTarget(0));

		listenerManagerExtreme.addListener(ControllerExtreme3D.UP8, () -> frontHookMotor.setControlTarget(0));
		
		//clawArm.startClawLimitThread();
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		autoChooser.addDefault("Far Can Grab", new FarCanGrabAuto());
		autoChooser.addObject("DualFar Can Grab", new DualFarCanGrabAuto());
		autoChooser.addObject("Drive Into Auto Zone", new DriveIntoAutoZoneAuto());
		autoChooser.addObject("Do Nothing", new DoNothingAuto());
		autoChooser.addObject("Dev Test Auto", new TestAuto());
	}

	@Override
	protected void updateDashboard()
	{
		//nothing, for now.
	}
}
