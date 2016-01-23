package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.hardware.misc.Piston;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerXbox;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class MainPneumaticsTest extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
	
	public Piston testPiston;
	
	
	public MainPneumaticsTest()
	{	
		listenerManagerExtreme = new ListenerManager(new Joystick(0), ControllerXbox.instance);	
		
		testPiston = new Piston(new Solenoid(0), new Solenoid(1));
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
	}

	protected void initializeDisabled()
	{
	}

	protected void initializeAuto()
	{
	}
	
	protected void initializeTeleop()
	{	
		testPiston.unlockPiston();
		
		listenerManagerExtreme.addListener(ControllerXbox.ADOWN, () -> testPiston.setPistonOn());
		
		listenerManagerExtreme.addListener(ControllerXbox.BDOWN, () -> testPiston.setPistonOff());

	}

	@Override
	protected void addAutoPrograms(SendableChooser autoChooser)
	{
	}

	@Override
	protected void updateDashboard()
	{
	}
}
