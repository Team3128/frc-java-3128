package org.team3128.main;

import java.util.LinkedList;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.autonomous.commands.CmdVisionGoTowardsCan;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.control.Always;
import org.team3128.listener.controller.ControllerXbox;
import org.team3128.util.ParticleReport;
import org.team3128.util.RoboVision;
import org.team3128.util.Units;

import com.ni.vision.NIVision.Range;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class MainCameraTest extends MainClass
{
	AxisCamera camera;
	RoboVision visionProcessor;
	
	public MotorGroup _pidTestMotor;
	
	public MotorGroup leftMotors;
	public MotorGroup rightMotors;
	public QuadratureEncoderLink leftDriveEncoder;
	public QuadratureEncoderLink rightDriveEncoder;
	public PowerDistributionPanel powerDistPanel;
	
	public TankDrive drive;
	
	int cameraHandle;
	PWMLights lights;
	
	ListenerManager manager;
	
	
	public MainCameraTest()
	{	
		manager = new ListenerManager(new Joystick(0), ControllerXbox.instance);
		
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
	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate)
	{
		camera = new AxisCamera("10.31.31.21");
		visionProcessor = new RoboVision(camera, .5, true);
		
		//trash can
//		SmartDashboard.putNumber("minH", 105);
//		SmartDashboard.putNumber("maxH", 137);
//		SmartDashboard.putNumber("minS", 5);
//		SmartDashboard.putNumber("maxS", 128);
//		SmartDashboard.putNumber("minV", 0);
//		SmartDashboard.putNumber("maxV", 255);
//		SmartDashboard.putNumber("aspectRatio",(21.9 * Units.in)/(28.8 * Units.in));
//		SmartDashboard.putNumber("rectangularityScore", 100);
		
/*		SmartDashboard.putNumber("minH", 35);
		SmartDashboard.putNumber("maxH", 70);
		SmartDashboard.putNumber("minS", 10);
		SmartDashboard.putNumber("maxS", 150);
		SmartDashboard.putNumber("minV", 128);
		SmartDashboard.putNumber("maxV", 255);*/
		
		SmartDashboard.putNumber("minH", 0);
		SmartDashboard.putNumber("maxH", 255);
		SmartDashboard.putNumber("minS", 0);
		SmartDashboard.putNumber("maxS", 255);
		SmartDashboard.putNumber("minV", 0);
		SmartDashboard.putNumber("maxV", 255);
		
//		SmartDashboard.putNumber("width", value);
		
		
		SmartDashboard.putNumber("aspectRatio", 1);
		SmartDashboard.putNumber("rectangularityScore", 78.5);
		
		robotTemplate.addListenerManager(manager);
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		CommandGroup followCanAuto = new CommandGroup();
		followCanAuto.addSequential(new CmdVisionGoTowardsCan(drive, visionProcessor));
		autoChooser.addDefault("Follow Can Auto", followCanAuto);
	}

	@Override
	protected void initializeDisabled()
	{
		
	}

	@Override
	protected void updateDashboard()
	{
		
	}

	@Override
	protected void initializeAuto()
	{
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initializeTeleop()
	{
		manager.addListener(Always.instance, () ->
		{
			LinkedList<ParticleReport> targets = visionProcessor.findSingleTarget(
					new Range(SmartDashboard.getInt("minH"), SmartDashboard.getInt("maxH")), 
	        		new Range(SmartDashboard.getInt("minS"), SmartDashboard.getInt("maxS")),
	        		new Range(SmartDashboard.getInt("minV"), SmartDashboard.getInt("maxV")),
	        		SmartDashboard.getNumber("aspectRatio",(21.9 * Units.in)/(28.8 * Units.in)),
	        		SmartDashboard.getNumber("rectangularityScore"));
			if(!targets.isEmpty())
			{
				
				ParticleReport targetReport = targets.get(0);
				
				SmartDashboard.putNumber("Target distance (in)", targetReport.computeDistanceHorizontal(21.9 * Units.in) /Units.in);
				SmartDashboard.putNumber("target heading angle", targetReport.getHeadingAngleOffset());
			}
			else
			{
				SmartDashboard.putNumber("Target distance (in)", 0);
				SmartDashboard.putNumber("target heading angle", 0);
			}
		});
		
		
		
	}

}
