package org.team3128.main;

import java.util.LinkedList;

import org.team3128.Log;
import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.util.ParticleReport;
import org.team3128.util.RoboVision;
import org.team3128.util.Units;

import com.ni.vision.NIVision.Range;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.AxisCamera;

public class MainCameraTest extends MainClass
{

	AxisCamera camera;
	
	RoboVision visionProcessor;
	
	public MainCameraTest()
	{

        // keep only green objects);

	}

	@Override
	protected void initializeRobot(RobotTemplate robotTemplate)
	{
		camera = new AxisCamera("10.31.31.23");
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
		
	}

	@Override
	protected void initializeAuto()
	{
		
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initializeTeleop()
	{
		LinkedList<ParticleReport> targets = visionProcessor.findSingleTarget(new Range(SmartDashboard.getInt("minH", 105), SmartDashboard.getInt("maxH", 137)), 
        		new Range(SmartDashboard.getInt("minS", 5), SmartDashboard.getInt("maxS", 50)),
        		new Range(SmartDashboard.getInt("minV", 0), SmartDashboard.getInt("maxV", 255)), (21.9 * Units.in)/(28.8 * Units.in), 100);
		
		ParticleReport targetReport = targets.get(0);
		
        Log.debug("RoboVision", "Target distance: " + targetReport.computeDistance() + " cm target heading angle");

	}

}
