package org.team3128.main;

import java.util.LinkedList;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.ParticleReport;
import org.team3128.util.RoboVision;
import org.team3128.util.Units;

import com.ni.vision.NIVision.Range;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Talon;
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
	
	
	public MainCameraTest()
	{	
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
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
		
	}

	@Override
	protected void initializeDisabled()
	{
		
	}

	@Override
	protected void updateDashboard()
	{
		LinkedList<ParticleReport> targets = visionProcessor.findSingleTarget(
				new Range(SmartDashboard.getInt("minH", 105), SmartDashboard.getInt("maxH", 137)), 
        		new Range(SmartDashboard.getInt("minS", 5), SmartDashboard.getInt("maxS", 128)),
        		new Range(SmartDashboard.getInt("minV", 0), SmartDashboard.getInt("maxV", 255)),
        		SmartDashboard.getNumber("aspectRatio",(21.9 * Units.in)/(28.8 * Units.in)),
        		SmartDashboard.getNumber("rectangularityScore", 100));
		if(!targets.isEmpty())
		{
			
			ParticleReport targetReport = targets.get(0);
			
	        Log.debug("RoboVision", "Target distance: " + targetReport.computeDistance() + " cm target heading angle: " + targetReport.getHeadingAngleOffset());
		}
	}

	@Override
	protected void initializeAuto()
	{
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initializeTeleop()
	{

	}

}
