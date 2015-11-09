package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.util.RoboVision;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class MainCameraTest extends MainClass
{

	AxisCamera camera;
	
	RoboVision visionProcessor;
	
	public MainCameraTest()
	{
		visionProcessor = new RoboVision();

	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate)
	{
		camera = new AxisCamera("10.31.28.11");
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
		
	}

	@Override
	protected void initializeAuto()
	{
		
	}

	@Override
	protected void initializeTeleop()
	{
		visionProcessor.targetRecognition(camera);
	}

}
