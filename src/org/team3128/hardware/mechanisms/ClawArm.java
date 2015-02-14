package org.team3128.hardware.mechanisms;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.AngleEndstopTarget;
import org.team3128.hardware.motor.speedcontrol.CurrentTarget;
import org.team3128.hardware.motor.speedcontrol.LinearAngleTarget;

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
	
	boolean inverted = true;
	
	/**
	 * indicates whether the claw is currently using manual or automatic control.
	 */
	boolean isUsingAutoControl;
	
	final double clawCurrentThreshold = .5;
	
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
		
		_clawGrab.setSpeedController(new CurrentTarget(panel, 12, clawCurrentThreshold));
		
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
	 * change arm from one side to the other
	 */
	public void switchArmToOtherSide()
	{
		inverted = !inverted;
	}
	
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
				_armJoint.setControlTarget(joyPower);
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
					_armRotate.setControlTarget((inverted ? 1 : -1) * joyPower);
				}
				else
				{
					_armRotate.startControl((inverted ? 1 : -1) * joyPower);
				}
			}
			else
			{
				_armRotate.setControlTarget(0);
			}
		}
	}
	
}
