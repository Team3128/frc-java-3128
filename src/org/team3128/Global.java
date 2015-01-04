package org.team3128;

import org.team3128.hardware.misc.TachLink;
import org.team3128.listener.ListenerManager;

import edu.wpi.first.wpilibj.Joystick;

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
	
	public Global()
	{	
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort));

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
