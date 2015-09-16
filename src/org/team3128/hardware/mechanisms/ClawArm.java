package org.team3128.hardware.mechanisms;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.hardware.motor.limiter.AngleLimiter;
import org.team3128.hardware.motor.limiter.SwitchLimiter;
import org.team3128.hardware.motor.logic.BlankSpeedLogic;
import org.team3128.hardware.motor.logic.PIDAngleLogic;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Mechanism to control our 2015 robot, The Clawwwwwww.  It basically manages the entire arm.
 * @author Jamie
 *
 */
public class ClawArm
{
	public MotorGroup  _armJoint, _clawGrab;
	
	CANJaguar armRotate;
		
	PIDAngleLogic armJointAngleTarget;
	
	AngleLimiter armRotateEndstopTarget;
	
	AngleLimiter armJointEndstopTarget;
	
	public IAngularEncoder _armRotateEncoder;
	
	public IAngularEncoder _armJointEncoder;
	
	public DigitalInput clawMinLimitSwitch;
	public DigitalInput clawMaxLimitSwitch;
	
	/**
	 * indicates whether the claw is currently using manual or automatic control.
	 */
	boolean jointUsingAutoControl, armUsingAutoControl;
	
	/**
	 * 
	 * @return true if the arm/shoulder is in auto mode
	 */
	public boolean isJointUsingAutoControl()
	{
		return jointUsingAutoControl;
	}

	/**
	 * 
	 * @return true if the joint/elbow is in auto mode
	 */
	public boolean isArmUsingAutoControl()
	{
		return armUsingAutoControl;
	}

	final double clawCurrentThreshold = 3;
	
	Thread clawLimitThread;
	
	final static double armMaxAngle = 300;
	
	final static double armMinAngle = 15;

	final static double armStepSize = 10;
	
	final static double shoulderTravelMiddle = 175;
	final static double elbowTravelMiddle = 140;

	/**
	 * Construct claw arm with given motors.
	 * 
	 * ClawArm will handle setting up the motors' speed controllers
	 * @param armRotate
	 * @param armJoint
	 * @param clawGrab
	 */
	public ClawArm(CANJaguar armRotate, MotorGroup armJoint, MotorGroup clawGrab, IAngularEncoder armEncoder, IAngularEncoder jointEncoder, PowerDistributionPanel panel)
	{
		this.armRotate = armRotate;
		_armJoint = armJoint;
		_clawGrab = clawGrab;
		
		clawMinLimitSwitch = new DigitalInput(9);
		clawMaxLimitSwitch = new DigitalInput(8);
		BlankSpeedLogic clawControl = new BlankSpeedLogic(0);
		clawControl.addLimiter(new SwitchLimiter(clawMinLimitSwitch, clawMaxLimitSwitch, false));
		_clawGrab.setSpeedController(clawControl);
		_clawGrab.startControl(0);
						
		armJointAngleTarget = new PIDAngleLogic(.009, 0, 0, 5, false, jointEncoder, false);
		
		armRotateEndstopTarget = new AngleLimiter(22, 295, 2, armEncoder);
		
		armJointEndstopTarget = new AngleLimiter(0, 300, 5, jointEncoder);
		
		_armRotateEncoder = armEncoder;
		
		_armJointEncoder = jointEncoder;
		
		switchArmToAutoControl();
		switchJointToManualControl();
		
	}
	
	public void closeClaw()
	{
		//the limit switch will stop it
		_clawGrab.setControlTarget(-1);
	}
	
	public void openClaw()
	{
		//the limit switch will stop it
		_clawGrab.setControlTarget(1);
	}
	
	public void stopClawLimitThread()
	{
		if(clawLimitThread != null)
		{
			if(clawLimitThread.isAlive())
			{
				clawLimitThread.interrupt();
				try
				{
					clawLimitThread.join();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startClawLimitThread()
	{
		stopClawLimitThread();
		clawLimitThread = new Thread(this::runClawLimitThread, "Claw Limit Thread");
		clawLimitThread.start();
	}
	
	private void runClawLimitThread()
	{
		while(true)
		{
			if(isOverHeightLimit(_armRotateEncoder.getAngle(), _armJointEncoder.getAngle()))
			{
				if(armUsingAutoControl && !jointUsingAutoControl)
				{
					double newAngle = Math.toDegrees(Math.acos((-1.444 * Math.cos(Math.toRadians(_armJointEncoder.getAngle() - elbowTravelMiddle)) + 1.1667)));
					setArmAngle(newAngle);
				}
				else if(!armUsingAutoControl && jointUsingAutoControl)
				{
					//created from the formula in isOverHeightLimit()
					double newAngle = Math.toDegrees(Math.acos((-.6923 * Math.cos(Math.toRadians(_armRotateEncoder.getAngle() - shoulderTravelMiddle)) + .8077)));
					_armJoint.setControlTarget(newAngle);
				}
			}
			
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}
	
	/**
	 * switch the arm to automatic, encoder-based control
	 */
	public
	void switchJointToAutoControl()
	{
		jointUsingAutoControl = true;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(armJointAngleTarget);
		_armJoint.startControl(_armJointEncoder.getAngle());
	}
	
	/**
	 * switch the arm to automatic, encoder-based control
	 */
	public void switchArmToAutoControl()
	{
		armUsingAutoControl = true;
		
		armRotate.setPositionMode(CANJaguar.kPotentiometer, .01, .000005, .0015);
		armRotate.configPotentiometerTurns(1);
	}
	
	/**
	 * switch the joint to manual motor power-based control
	 */
	public void switchJointToManualControl()
	{
		jointUsingAutoControl = false;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(null);
		//_armJoint.startControl(0);
	}
	
	/**
	 * switch the arm to manual motor power-based control
	 */
	public void switchArmToManualControl()
	{
		armUsingAutoControl = false;

		armRotate.setPercentMode(CANJaguar.kPotentiometer);
		//_armRotate.startControl(0);
	}
	
	/**
	 * set shoulder rotation in perspective of front
	 */
	
	/**
	 * Set the arm to a certain rotation angle
	 * @param degreesToSet
	 */
	public void setArmAngle(double degreesToSet)
	{
		if(!armUsingAutoControl)
		{
			switchArmToAutoControl();
		}
		
		armRotate.set(degreesToSet / 360.0);
	}
	
	/**
	 * Set the joint to a certain rotation angle
	 * @param degreesToSet
	 */
	public void setJointAngle(double degreesToSet)
	{
		if(!jointUsingAutoControl)
		{
			switchJointToAutoControl();
		}
		
		_armJoint.startControl(degreesToSet);
	}
	
	private boolean isOverHeightLimit(double armAngle, double jointAngle)
	{
		//convert from 0 to 300 to -150 to 150
		armAngle -= shoulderTravelMiddle;
		jointAngle -= elbowTravelMiddle;
		
		double result = (34 * Units.in) +
				(Math.cos(Math.toRadians(armAngle)) * 36 * Units.in) +
				(Math.cos(Math.toRadians(jointAngle)) * 52 * Units.in);
		return result > 76 * Units.in;
	}
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onJointJoyInput(double joyPower)
	{
		if(jointUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				switchJointToManualControl();
			}
		}
		if(!jointUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				_armJoint.setControlTarget(joyPower);
			}
			else
			{
				//switchJointToAutoControl();
				_armJoint.setControlTarget(0);
			}
		}
	}
	
	/**
	 * Use joystick input to rotate arm.
	 */
	public void onArmJoyInput(double joyPower)
	{	
		if(armUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				switchArmToManualControl();
			}
		}
		else
		{
			if(Math.abs(joyPower) >= .1)
			{
				armRotate.set(joyPower);
			}
			else
			{
				switchArmToAutoControl();
				//_armRotate.setControlTarget(0);
			}
		}
	}

	/**
	 * Reset the control targets to the current position.
	 * 
	 * This is so that the robot will not shift once the motors are enabled if the mechanism has been moved manually.
	 */
	public void resetTargets()
	{
		if(armUsingAutoControl)
		{
			armRotate.set(armRotate.getPosition());
		}
		else
		{
			armRotate.set(0);
		}
		
		
		if(jointUsingAutoControl)
		{
			_armJoint.setControlTarget(_armJointEncoder.getAngle());
		}
		else
		{
			_armJoint.setControlTarget(0);
		}
		
	}
}
