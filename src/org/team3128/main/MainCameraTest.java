package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.util.RoboVision;

import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class MainCameraTest extends MainClass
{

	int cameraHandle;
	
	RoboVision visionProcessor;
	
	public MainCameraTest()
	{
		visionProcessor = new RoboVision();

	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate)
	{
        cameraHandle = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(cameraHandle);
	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
        NIVision.IMAQdxStartAcquisition(cameraHandle);
		
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
		visionProcessor.targetRecognition(cameraHandle);
	}

}
