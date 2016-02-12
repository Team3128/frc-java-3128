package org.team3128.main;

import org.team3128.MainClass;
import org.team3128.RobotTemplate;
import org.team3128.hardware.misc.Piston;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerXbox;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class MainPneumaticsTest extends MainClass
{
	
	public ListenerManager listenerManagerExtreme;
	
	public Piston testPiston;
	
	public Compressor compressor;
	
	public MainPneumaticsTest()
	{	
		listenerManagerExtreme = new ListenerManager(new Joystick(0));	
		
		testPiston = new Piston(new Solenoid(0), new Solenoid(1));
		testPiston.invertPiston();
		
		compressor = new Compressor();
		compressor.setClosedLoopControl(true);
	}

	protected void initializeRobot(RobotTemplate robotTemplate)
	{	
		robotTemplate.addListenerManager(listenerManagerExtreme);
		testPiston.setPistonOff();
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
		
		listenerManagerExtreme.addListener(ControllerXbox.LBDOWN, () -> compressor.stop());
		listenerManagerExtreme.addListener(ControllerXbox.RBDOWN, () -> compressor.start());

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
