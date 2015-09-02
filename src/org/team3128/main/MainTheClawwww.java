package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotProperties;
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
import org.team3128.util.RoboVision;
import org.team3128.util.Units;

import com.ni.vision.NIVision;

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
	//public ListenerManager listenerManagerJoyLeft;
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

	public AnalogPotentiometerEncoder armRotateEncoder;
	
	public AnalogPotentiometerEncoder armJointEncoder;
	
	public PowerDistributionPanel powerDistPanel;
	
	public ArcadeDrive drive;
	
	public ClawArm clawArm;
	
	IListenerCallback updateDriveArcade;
	IListenerCallback updateDriveCOD;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	int cameraHandle;
	RoboVision visionProcessor; 
	
	public MainTheClawwww()
	{	
		listenerManagerExtreme = new ListenerManager(new Joystick(RobotProperties.controllerPort), ControllerExtreme3D.instance);
		//listenerManagerJoyLeft = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);
		listenerManagerJoyRight = new ListenerManager(new Joystick(1), ControllerAttackJoy.instance);		
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
		armTurnMotor.reverseMotor();
		
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

		drive = new ArcadeDrive(leftMotors, rightMotors, listenerManagerExtreme);
		
		updateDriveCOD = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			drive.steer(joyX, joyY, throttle, listenerManagerExtreme.getRawBool(ControllerExtreme3D.DOWN2));
		};
		
		visionProcessor = new RoboVision();
		
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
		//robotTemplate.addListenerManager(listenerManagerJoyLeft);
		robotTemplate.addListenerManager(listenerManagerJoyRight);
		
        cameraHandle = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(cameraHandle);
        
        //Set options class
        RobotProperties.wheelCircumfrence = 6 * Units.INCH * Math.PI;
        RobotProperties.wheelBase = 24.5 * Units.INCH;
		
        Log.info("MainTheClawwww", "\"The Clawwwwwww.....\"   Activated");
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
		//lights.setColor(Color.new4Bit(0xa, 2, 2));
	}
	
	protected void initializeTeleop()
	{	
		//lights.setFader(Color.new11Bit(2000, 2000, 2000), 1, 10);
        NIVision.IMAQdxStartAcquisition(cameraHandle);
		
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
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.JOYX, () ->
		{
			double power = listenerManagerJoyRight.getRawAxis(ControllerAttackJoy.JOYX);
			clawArm.onJointJoyInput((elbowInverted ? armSpeedMultiplier : -armSpeedMultiplier) * power);
		});
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN2, () -> shoulderInverted = false);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN3, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN6, () -> shoulderInverted = true);
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN7, () -> shoulderInverted = false);
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN1, () -> clawGrabMotor.setControlTarget(0.7));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP1, () -> clawGrabMotor.setControlTarget(0));
		
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN4, () -> clawGrabMotor.setControlTarget(0.7));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.UP4, () -> clawGrabMotor.setControlTarget(0));
		listenerManagerJoyRight.addListener(ControllerAttackJoy.DOWN5, () -> clawGrabMotor.setControlTarget(-0.7));
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

		listenerManagerExtreme.addListener(Always.instance, () -> visionProcessor.targetRecognition(cameraHandle));
		
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
		SmartDashboard.putData("armRotateEncoder", armRotateEncoder);
		SmartDashboard.putNumber("Total Current: ", powerDistPanel.getTotalCurrent());
	}
}
