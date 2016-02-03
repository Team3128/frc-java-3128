package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.autonomous.programs.DoNothingAuto;
import org.team3128.autonomous.programs.DriveIntoAutoZoneAuto;
import org.team3128.autonomous.programs.DualFarCanGrabAuto;
import org.team3128.autonomous.programs.FarCanGrabAuto;
import org.team3128.autonomous.programs.TakeToteIntoZoneAuto;
import org.team3128.autonomous.programs.TestAuto;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.angular.AnalogPotentiometerEncoder;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.LightsColor;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.control.Always;
import org.team3128.listener.controller.ControllerAttackJoy;
import org.team3128.listener.controller.ControllerExtreme3D;
import org.team3128.util.RobotMath;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for our 2015 robot, The Clawwww.
 * @author Jamie
 *
 */
public class MainTheClawwww extends MainClass
{
	
	/**
	 * Multiplier for teleop arm speed
	 */
	public static double armSpeedMultiplier = .8;
	
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
	
	public MotorGroup frontHookMotor;
	
	public MotorGroup clawGrabMotor;

	public IAngularEncoder armRotateEncoder;
	
	public AnalogPotentiometerEncoder armJointEncoder;
	
	public PowerDistributionPanel powerDistPanel;
	
	public TankDrive drive;
	
	public ClawArm clawArm;
	
	IListenerCallback updateDriveArcade;
	IListenerCallback updateDriveCOD;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	PWMLights lights;
	
	public MainTheClawwww()
	{	
		listenerManagerExtreme = new ListenerManager(ControllerExtreme3D.instance, new Joystick(0));
		listenerManagerJoyLeft = new ListenerManager(ControllerAttackJoy.instance, new Joystick(2));
		listenerManagerJoyRight = new ListenerManager(ControllerAttackJoy.instance, new Joystick(1));		
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
		rightMotors.invert();
		//rightMotors.startControl(0);
		
		armTurnMotor = new MotorGroup();
		armTurnMotor.addControlledMotor(new Talon(6));
		armTurnMotor.invert();
		

		armRotateEncoder = new AnalogPotentiometerEncoder(0, 0, 4.829, 300);
		
		armJointMotor = new MotorGroup();
		armJointMotor.addControlledMotor(new Talon(5));
		
		armJointEncoder = new AnalogPotentiometerEncoder(1, 0, 4.829, 300);
		
		frontHookMotor = new MotorGroup();
		frontHookMotor.addControlledMotor(new Talon(9));
		
		clawGrabMotor = new MotorGroup();
		clawGrabMotor.addControlledMotor(new Talon(8));
		
		//lights = new PWMLights(0, 9, 7);

		clawArm = new ClawArm(armTurnMotor, armJointMotor, clawGrabMotor, armRotateEncoder, armJointEncoder, powerDistPanel);

		drive = new TankDrive(leftMotors, rightMotors, leftDriveEncoder, rightDriveEncoder, 6 * Units.in * Math.PI, 24.5 * Units.in);
		
		updateDriveCOD = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			drive.arcadeDrive(joyX, joyY, throttle, listenerManagerExtreme.getRawBool(ControllerExtreme3D.DOWN2));
		};
		
		
		lights = new PWMLights(10, 11, 12);

	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		robotTemplate.addListenerManager(listenerManagerJoyLeft);
		robotTemplate.addListenerManager(listenerManagerJoyRight);
		
        Log.info("MainTheClawwww", "\"The Clawwwwwww.....\"   Activated");
	}

	protected void initializeDisabled()
	{
		
		armTurnMotor.resetSpeedControl();
		armJointMotor.resetSpeedControl();
		clawArm.switchJointToManualControl();
		
		clawArm.stopClawLimitThread();
				
		leftMotors.resetSpeedControl();
		rightMotors.resetSpeedControl();
		
		clawArm.resetTargets();
	}

	protected void initializeAuto()
	{
		//lights.setColor(Color.new4Bit(0xa, 2, 2));
		
		//reset PID error
		armTurnMotor.resetSpeedControl();
		clawArm.resetTargets();
	}
	
	protected void initializeTeleop()
	{	
		clawArm.resetTargets();

		//lights.setFader(Color.new11Bit(2000, 2000, 2000), 1, 10);
		
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
		
					
		//-----------------------------------------------------------
		// Arm control code, on joysticks
		//-----------------------------------------------------------
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = (shoulderInverted ? armSpeedMultiplier : -armSpeedMultiplier) * listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYY);
			
			if(power > 0)
			{
				power /= 1.5;
			}
			
			//lights.setColor(Color.new8Bit(0x33, 0x33, ((int)armJointEncoder.getAngle() * (0xff / 330))));
			
			
			clawArm.onArmJoyInput(power);
			
			
		});
		
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.JOYY, () ->
		{
			double power = listenerManagerJoyLeft.getRawAxis(ControllerAttackJoy.JOYY);
			clawArm.onJointJoyInput((elbowInverted ? armSpeedMultiplier : -armSpeedMultiplier) * power);
		});
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN2, () -> shoulderInverted = false);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN3, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN6, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN7, () -> shoulderInverted = false);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN2, () -> elbowInverted = false);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN3, () -> elbowInverted = true);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN6, () -> elbowInverted = true);
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN7, () -> elbowInverted = false);
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(0.8));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(-0.8));
		listenerManagerJoyLeft.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN4, () -> clawGrabMotor.setControlTarget(0.8));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP4, () -> clawGrabMotor.setControlTarget(0));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN5, () -> clawGrabMotor.setControlTarget(-0.8));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP5, () -> clawGrabMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN3, () -> frontHookMotor.setControlTarget(0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP3, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN4, () -> frontHookMotor.setControlTarget(-0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP4, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN5, () -> frontHookMotor.setControlTarget(0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP5, () -> frontHookMotor.setControlTarget(0));
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN6, () -> frontHookMotor.setControlTarget(-0.3));
		listenerManagerExtreme.addListener(ControllerExtreme3D.UP6, () -> frontHookMotor.setControlTarget(0));

		listenerManagerExtreme.addListener(ControllerExtreme3D.UP8, () -> frontHookMotor.setControlTarget(0));

		listenerManagerExtreme.addListener(Always.instance, () -> {
			int red = RobotMath.clampInt(RobotMath.floor_double_int(255 * (powerDistPanel.getTotalCurrent() / 30.0)), 0, 255);
			int green = 255 - red;
			
			LightsColor color = LightsColor.new8Bit(red, green, 0);
			lights.setColor(color);
			
			//Log.debug("ArmAngle", armRotateEncoder.getAngle() + " degrees");
		});
		
		//clawArm.startClawLimitThread();
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		autoChooser.addDefault("Take Tote into Auto Zone", new TakeToteIntoZoneAuto(drive, frontHookMotor, lights));
		autoChooser.addObject("Can Grab", new FarCanGrabAuto(drive, clawArm, frontHookMotor, false));
		autoChooser.addObject("Can Grab w/ Tote Pickup", new FarCanGrabAuto(drive, clawArm, frontHookMotor, true));
		autoChooser.addObject("Dual Can Grab", new DualFarCanGrabAuto(drive, clawArm));
		autoChooser.addObject("Drive Into Auto Zone", new DriveIntoAutoZoneAuto(drive, lights));
		autoChooser.addObject("Do Nothing", new DoNothingAuto(lights));
		autoChooser.addObject("Dev Test Auto", new TestAuto());
	}

	@Override
	protected void updateDashboard()
	{
		SmartDashboard.putNumber("armRotateEncoder", armRotateEncoder.getAngle());
		SmartDashboard.putNumber("Total Current: ", powerDistPanel.getTotalCurrent());
	}
}
