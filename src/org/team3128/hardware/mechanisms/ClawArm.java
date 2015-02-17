package org.team3128.hardware.mechanisms;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.AngleEndstopTarget;
import org.team3128.hardware.motor.speedcontrol.LinearAngleTarget;
import org.team3128.util.Units;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class ClawArm
{
	MotorLink _armRotate, _armJoint, _clawGrab;
	
	LinearAngleTarget armRotateAngleTarget;
	
	LinearAngleTarget armJointAngleTarget;
	
	AngleEndstopTarget armRotateEndstopTarget;
	
	AngleEndstopTarget armJointEndstopTarget;
	
	IAngularEncoder _armRotateEncoder;
	
	IAngularEncoder _armJointEncoder;
	
	/**
	 * indicates whether the claw is currently using manual or automatic control.
	 */
	boolean jointUsingAutoControl, armUsingAutoControl;
	
	final double clawCurrentThreshold = 3;
	
	Thread clawLimitThread;
	
	final static double armMaxAngle = 300;
	
	final static double armMinAngle = 15;

	final static double armStepSize = 10;

	/**
	 * Construct claw arm with given motors.
	 * 
	 * ClawArm will handle setting up the motors' speed controllers
	 * @param armRotate
	 * @param armJoint
	 * @param clawGrab
	 */
	public ClawArm(MotorLink armRotate, MotorLink armJoint, MotorLink clawGrab, IAngularEncoder armEncoder, IAngularEncoder jointEncoder, PowerDistributionPanel panel)
	{
		_armRotate = armRotate;
		_armJoint = armJoint;
		_clawGrab = clawGrab;
		
		//_clawGrab.setSpeedController(new CurrentTarget(panel, 10, clawCurrentThreshold, 60));
		
		armRotateAngleTarget = new LinearAngleTarget(.02, 4, false, armEncoder);
		
		armJointAngleTarget = new LinearAngleTarget(.02, 5, false, jointEncoder);
		
		armRotateEndstopTarget = new AngleEndstopTarget(22, 295, 2, armEncoder);
		
		armJointEndstopTarget = new AngleEndstopTarget(0, 300, 5, jointEncoder);
		
		_armRotateEncoder = armEncoder;
		
		_armJointEncoder = jointEncoder;
		
		switchArmToAutoControl();
		switchJointToAutoControl();
		
	}
	
	public void stopClawLimitThread()
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
	
	public void startClawLimitThread()
	{
		if(clawLimitThread != null)
		{
			stopClawLimitThread();
		}
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
					double newAngle = Math.toDegrees(Math.acos((-1.444 * Math.cos(Math.toRadians(_armJointEncoder.getAngle())) + 1.1667)));
					_armRotate.setControlTarget(newAngle);
				}
				else if(!armUsingAutoControl && jointUsingAutoControl)
				{
					//created from the formula in isOverHeightLimit()
					double newAngle = Math.toDegrees(Math.acos((-.6923 * Math.cos(Math.toRadians(_armRotateEncoder.getAngle())) + .8077)));
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
	private void switchJointToAutoControl()
	{
		jointUsingAutoControl = true;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(armJointAngleTarget);
		_armJoint.startControl(_armJointEncoder.getAngle());
	}
	
	/**
	 * switch the arm to automatic, encoder-based control
	 */
	private void switchArmToAutoControl()
	{
		armUsingAutoControl = true;
		
		_armRotate.stopSpeedControl();
		_armRotate.setSpeedController(armRotateAngleTarget);
		_armRotate.startControl(_armRotateEncoder.getAngle());
	}
	
	/**
	 * switch the joint to manual motor power-based control
	 */
	private void switchJointToManualControl()
	{
		jointUsingAutoControl = false;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(armJointEndstopTarget);
		_armJoint.startControl(0);
	}
	
	/**
	 * switch the arm to manual motor power-based control
	 */
	private void switchArmToManualControl()
	{
		armUsingAutoControl = false;

		_armRotate.stopSpeedControl();
		_armRotate.setSpeedController(armRotateEndstopTarget);
		_armRotate.startControl(0);
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
		
		_armRotate.startControl(degreesToSet);
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
		armAngle -= 175;
		jointAngle -= 140;
		
		double result = (34 * Units.INCH) +
				(Math.cos(Math.toRadians(armAngle)) * 36 * Units.INCH) +
				(Math.cos(Math.toRadians(jointAngle)) * 52 * Units.INCH);
		return result > 76 * Units.INCH;
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
				switchJointToAutoControl();
			}
		}
	}
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onArmJoyInput(double joyPower)
	{
		if(isOverHeightLimit(((5 * RobotMath.sgn(joyPower)) + _armRotateEncoder.getAngle()), _armJointEncoder.getAngle()))
		{
			Log.info("ClawArm", "Arm rotate move would put claw over height limit!");
			if(!armUsingAutoControl)
			{
				switchArmToAutoControl();
			}
			return;
		}
		
		if(armUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				switchArmToManualControl();
			}
		}
		if(!armUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				_armRotate.startControl(joyPower);
			}
			else
			{
		    	switchArmToAutoControl();
			}
		}
	}
}
