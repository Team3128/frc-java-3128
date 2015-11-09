package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.LightsColor;
import org.team3128.hardware.lights.LightsSequence;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerExtreme3D;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for our 2015 robot, The Clawwww minus the Claw (www) ( just drive ).
 * @author Jamie (modified by Wesley)
 *
 */
public class MainDriveCold extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
	
	public MotorGroup _pidTestMotor;
	
	public MotorGroup leftMotors;
	public MotorGroup rightMotors;
	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	public PowerDistributionPanel powerDistPanel;
	
	public TankDrive drive;
	
	IListenerCallback updateDriveArcade;
	IListenerCallback updateDriveCOD;
	
	boolean codDriveEnabled = false;
	boolean shoulderInverted = true;
	boolean elbowInverted = true;
	
	int cameraHandle;
	PWMLights lights;
	
	LightsSequence lightShowSequence;
	
	
	public MainDriveCold()
	{	
		listenerManagerExtreme = new ListenerManager(new Joystick(0), ControllerExtreme3D.instance);	
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(3, 4, 128, true);
		
		leftMotors = new MotorGroup();
		leftMotors.addControlledMotor(new Talon(1));
		leftMotors.addControlledMotor(new Talon(2));
		
		
		rightMotors = new MotorGroup();
		rightMotors.addControlledMotor(new Talon(3));
		rightMotors.addControlledMotor(new Talon(4));
		rightMotors.invert();
	
		drive = new TankDrive(leftMotors, rightMotors, leftDriveEncoder, rightDriveEncoder, 6 * Units.in * Math.PI, 24.5 * Units.in);
		
		updateDriveCOD = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.TWIST);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			drive.arcadeDrive(joyX, joyY, throttle, listenerManagerExtreme.getRawBool(ControllerExtreme3D.DOWN2));
		};
				
		lights = new PWMLights(10, 11, 12);
		
		lightShowSequence = new LightsSequence();

		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0xff, 1, 1), 500, false));
		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0xff, 0xff, 1), 500, false));
		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(1, 0xff, 1), 500, false));
		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(1, 0xff, 0xff), 500, false));
		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(1, 1, 0xff), 500, false));
		lightShowSequence.addStep(new LightsSequence.Step(LightsColor.new8Bit(0xff, 1, 0xff), 500, false));
		
		lightShowSequence.setRepeat(true);
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		
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
		
		lights.executeSequence(lightShowSequence);

		//lights.setFader(Color.new11Bit(2000, 2000, 2000), 1, 10);
        //NIVision.IMAQdxStartAcquisition(cameraHandle);
		
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
