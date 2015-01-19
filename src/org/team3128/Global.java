package org.team3128;

import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.AnalogPotentiometerEncoder;
import org.team3128.hardware.misc.TachLink;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.Listenable;
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
	public AnalogPotentiometerEncoder _testPot;
	
	
	//public HolonomicDrive _drive;
	
	public ArcadeDrive _drive;
	
	public Global()
	{	
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort));
		
		_motorLeftFront = new MotorLink(new Talon(1));
		_motorLeftBack = new MotorLink(new Talon(4));
		_motorRightFront = new MotorLink(new Talon(2));
		_motorRightBack = new MotorLink(new Talon(3));
		
		//_drive = new HolonomicDrive(_motorLeftFront, _motorLeftBack, _motorRightFront, _motorRightBack, _listenerManager);
		_testPot = new AnalogPotentiometerEncoder(0);
		_drive = new ArcadeDrive(_listenerManager);
		
		_drive.addLeftMotor(_motorLeftFront);
		_drive.addLeftMotor(_motorLeftBack);
		_drive.addRightMotor(_motorRightFront);
		_drive.addRightMotor(_motorRightBack);

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
		IListenerCallback updateDrive = () -> _drive.steer();
		
		_listenerManager.addListener(Listenable.JOY1X, updateDrive);
		_listenerManager.addListener(Listenable.JOY1Y, updateDrive);
		_listenerManager.addListener(Listenable.JOY2Y, updateDrive); 
		_listenerManager.addListener(Listenable.ALWAYS, _testPot.printPot);
		_listenerManager.addListener(Listenable.ADOWN, _testPot.zeroPot);
	}
}
