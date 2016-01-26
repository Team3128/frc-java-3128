package org.team3128.main;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.misc.Piston;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerExtreme3D;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for our 2015 robot, The Clawwww minus the Claw (www) ( just drive ).
 * @author Jamie (modified by Wesley)
 *
 */
public class MainFlyingSwallow extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
		
	public MotorGroup leftMotors;
	public MotorGroup rightMotors;
	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	public PowerDistributionPanel powerDistPanel;
	
	public TankDrive drive;
	
	IListenerCallback updateDriveCOD;
	
	Piston gearshiftPiston, intakePiston;
		
	public MainFlyingSwallow()
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
	
		drive = new TankDrive(leftMotors, rightMotors, leftDriveEncoder, rightDriveEncoder, 8 * Units.in * Math.PI, 24.5 * Units.in);
		
		gearshiftPiston = new Piston(new Solenoid(0), new Solenoid(1));
		
		updateDriveCOD = () ->
		{
			double joyX = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYX);
			double joyY = listenerManagerExtreme.getRawAxis(ControllerExtreme3D.JOYY);
			double throttle = -listenerManagerExtreme.getRawAxis(ControllerExtreme3D.THROTTLE);
			
			drive.arcadeDrive(joyX, joyY, throttle, listenerManagerExtreme.getRawBool(ControllerExtreme3D.TRIGGERDOWN));
		};
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		
        Log.info("MainFlyingSwallow", "Activating the Flying Swallow");
        Log.info("MainFlyingSwallow", "...but which one, an African or a European?");
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
		listenerManagerExtreme.addListener(ControllerExtreme3D.JOYX, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.JOYY, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.THROTTLE, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.TRIGGERDOWN, updateDriveCOD);
		listenerManagerExtreme.addListener(ControllerExtreme3D.TRIGGERUP, updateDriveCOD);

		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN12, () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN2, () -> gearshiftPiston.invertPiston());
		listenerManagerExtreme.addListener(ControllerExtreme3D.DOWN3, () -> intakePiston.invertPiston());

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
