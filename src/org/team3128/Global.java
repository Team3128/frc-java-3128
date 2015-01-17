package org.team3128;

import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.velocity.TachLink;
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
	
	public MotorLink leftMotors;

	public MotorLink _motorLeftBack;

	public MotorLink rightMotors;

	public MotorLink _motorRightBack;
	
	//public HolonomicDrive _drive;
	
	public ArcadeDrive _drive;
	
	public Global()
	{	
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort));
		
		leftMotors = new MotorLink();
		leftMotors.addControlledMotor(new Talon(1));
		leftMotors.addControlledMotor(new Talon(4));
		rightMotors = new MotorLink();
		rightMotors.addControlledMotor(new Talon(2));
		rightMotors.addControlledMotor(new Talon(3));
		
		//_drive = new HolonomicDrive(_motorLeftFront, _motorLeftBack, _motorRightFront, _motorRightBack, _listenerManager);
		
		_drive = new ArcadeDrive(leftMotors, rightMotors, _listenerManager);

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
	}
}
