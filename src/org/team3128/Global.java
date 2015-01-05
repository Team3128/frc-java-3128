package org.team3128;

import org.team3128.hardware.misc.TachLink;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.ListenerManager;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;

/**
 * The Global class is where all of the hardware objects that represent the robot are stored.
 * It also sets up the control bindings.
 * 
 * Its functions are called only by RobotTemplate.
 * @author Jamie
 *
 */
public class Global
{
	public ListenerManager _listenerManager;
	
	public MotorLink _motorLeftFront;

	public MotorLink _motorLeftBack;

	public MotorLink _motorRightFront;

	public MotorLink _motorRightBack;

	
	public Global()
	{	
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort));
		
		_motorLeftFront = new MotorLink(new Talon(0));
		_motorLeftBack = new MotorLink(new Talon(1));
		_motorRightFront = new MotorLink(new Talon(2));
		_motorRightBack = new MotorLink(new Talon(3));

	}

	void initializeRobot()
	{
		
		TachLink link = new TachLink(0, 54);
		
		Log.debug("Global", "Tachometer: " + link.getRaw());
	}

	void initializeDisabled()
	{

	}

	void initializeAuto()
	{
		new Thread(() -> AutoConfig.initialize(this), "AutoConfig").start();
	}

	void initializeTeleop()
	{
	}
}
