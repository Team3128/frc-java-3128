package org.team3128.hardware.mechanisms;

import org.team3128.Log;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.AngleEndstopTarget;
import org.team3128.hardware.motor.speedcontrol.CurrentTarget;
import org.team3128.hardware.motor.speedcontrol.LinearAngleTarget;
import org.team3128.util.RobotMath;
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
	boolean isUsingAutoControl;
	
	final double clawCurrentThreshold = 3;
	
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
		
		_clawGrab.setSpeedController(new CurrentTarget(panel, 10, clawCurrentThreshold, 60));
		
		armRotateAngleTarget = new LinearAngleTarget(.4, 3, armEncoder);
		
		armJointAngleTarget = new LinearAngleTarget(0, 2, jointEncoder);
		
		armRotateEndstopTarget = new AngleEndstopTarget(22, 295, 2, armEncoder);
		
		armJointEndstopTarget = new AngleEndstopTarget(30, 300, 5, jointEncoder);
		
		_armRotateEncoder = armEncoder;
		
		_armJointEncoder = jointEncoder;
		
		switchToManualControl();
	}
	
	/**
	 * switch the arm to automatic, encoder-based control
	 */
	private void switchToAutoControl()
	{
		isUsingAutoControl = true;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(null);
		
		_armRotate.stopSpeedControl();
		_armRotate.setSpeedController(armRotateAngleTarget);
	}
	
	/**
	 * switch the arm to manual motor power-based control
	 */
	private void switchToManualControl()
	{
		isUsingAutoControl = false;
		
		_armJoint.stopSpeedControl();
		_armJoint.setSpeedController(null);
		
		_armRotate.stopSpeedControl();
		_armRotate.setSpeedController(armRotateEndstopTarget);
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
		if(!isUsingAutoControl)
		{
			switchToAutoControl();
		}
		
		_armRotate.startControl(degreesToSet);
	}
	
	/**
	 * Set the joint to a certain rotation angle
	 * @param degreesToSet
	 */
	public void setJointAngle(double degreesToSet)
	{
		if(!isUsingAutoControl)
		{
			switchToAutoControl();
		}
		
		_armJoint.startControl(degreesToSet);
	}
	
	private boolean isOverHeightLimit(double armAngle, double jointAngle)
	{
		//convert from 0 to 300 to -150 to 150
		armAngle -= 150;
		jointAngle -= 150;
		
		double result = (34 * Units.INCH) +
				(Math.cos(Math.toRadians(armAngle)) * 36 * Units.INCH) +
				(Math.cos(Math.toRadians(jointAngle)) * 52 * Units.INCH);
		return result > 76 * Units.INCH;
	}
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onArmJointJoyInput(double joyPower)
	{
		if(isUsingAutoControl)
		{
			if(!_armRotate.isSpeedControlRunning()/* && !_armJoint.isSpeedControlRunning()*/)
			{
				switchToManualControl();
			}
		}
		if(!isUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				if(isOverHeightLimit(_armRotateEncoder.getAngle(), ((2 * RobotMath.sgn(joyPower)) + _armJointEncoder.getAngle())))
				{
					Log.info("ClawArm", "Arm joint move would put claw over height limit!");
					_armJoint.setControlTarget(0);
				}
				else
				{
					_armJoint.setControlTarget(joyPower);
				}
			}
			else
			{
				_armJoint.setControlTarget(0);
			}
		}
	}
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onArmRotateJoyInput(double joyPower)
	{
		if(isUsingAutoControl)
		{
			if(!_armRotate.isSpeedControlRunning() /*&& !_armJoint.isSpeedControlRunning()*/)
			{
				switchToManualControl();
			}
		}
		if(!isUsingAutoControl)
		{
			if(Math.abs(joyPower) >= .1)
			{
				if(_armRotate.isSpeedControlRunning())
				{
					_armRotate.setControlTarget(joyPower);
				}
				else
				{
					if(isOverHeightLimit(((2 * RobotMath.sgn(joyPower)) + _armRotateEncoder.getAngle()), _armJointEncoder.getAngle()))
					{
						Log.info("ClawArm", "Arm rotate move would put claw over height limit!");
						_armJoint.setControlTarget(0);
					}
					else
					{
						_armRotate.startControl(joyPower);
					}
				}
			}
			else
			{
				_armRotate.setControlTarget(0);
			}
		}
	}
	
}
