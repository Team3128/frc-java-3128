package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.drive.HolonomicDrive;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerExtreme3D;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Holonomic drive test robot once numbered 3129
 * @author Jamie (modified by Wesley)
 *
 */
public class MainDriveHolo extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
	
	public MotorGroup _pidTestMotor;
	
	public MotorGroup leftFrontMotors;
	public MotorGroup leftBackMotors;
	public MotorGroup rightFrontMotors;
	public MotorGroup rightBackMotors;
	public PowerDistributionPanel powerDistPanel;
	
	public HolonomicDrive holo;
	
	IListenerCallback updateDriveHolo;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	
	public MainDriveHolo()
	{	
		listenerManagerExtreme = new ListenerManager(ControllerExtreme3D.instance, new Joystick(0));
		powerDistPanel = new PowerDistributionPanel();
		
		holo = new HolonomicDrive(leftFrontMotors, leftBackMotors, rightFrontMotors, rightBackMotors, listenerManagerExtreme);
		
		updateDriveHolo = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYX);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double twist = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			holo.steer(joyX, joyY, twist, throttle);
		};
		
		leftFrontMotors = new MotorGroup();
		leftBackMotors = new MotorGroup();
		rightFrontMotors = new MotorGroup();
		rightBackMotors = new MotorGroup();

		leftFrontMotors.addControlledMotor(new Victor(0));
		leftBackMotors.addControlledMotor(new Victor(1));
		rightFrontMotors.addControlledMotor(new Victor(2));
		rightBackMotors.addControlledMotor(new Victor(3));
		
		holo = new HolonomicDrive(leftFrontMotors, leftBackMotors, rightFrontMotors, rightBackMotors, listenerManagerExtreme);
		
		updateDriveHolo = () ->
		{
			double joyX = -1 * listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYX);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double twist = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double throttle = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			holo.steer(joyX, twist, joyY, throttle);
		};
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		
        //cameraHandle = NIVision.IMAQdxOpenCamera("cam0",
        //        NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        //NIVision.IMAQdxConfigureGrab(cameraHandle);
		
        Log.info("MainDriveCold", "\"Coldbot\"   Activated");
	}

	protected void initializeDisabled()
	{
	}

	protected void initializeAuto()
	{
	}
	
	protected void initializeTeleop()
	{	
		
			
		//-----------------------------------------------------------
		// Drive code, on Logitech Extreme3D joystick
		//-----------------------------------------------------------
		listenerManagerExtreme.addListener(ControllerExtreme3D.JOYX, updateDriveHolo);
		listenerManagerExtreme.addListener(ControllerExtreme3D.JOYY, updateDriveHolo);
		listenerManagerExtreme.addListener(ControllerExtreme3D.TWIST, updateDriveHolo);
		listenerManagerExtreme.addListener(ControllerExtreme3D.THROTTLE, updateDriveHolo);

		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN12, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
	}

	@Override
	protected void updateDashboard()
	{
		SmartDashboard.putNumber("Total Current: ", powerDistPanel.getTotalCurrent());
	}
}
