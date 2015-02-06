package org.team3128.hardware.misc;

import org.team3128.hardware.motor.MotorLink;

public class ClawArm
{
	MotorLink _armRotate, _armJoint, _clawGrab;
	
	boolean inverted;
	
	public ClawArm(MotorLink armRotate, MotorLink armJoint, MotorLink clawGrab)
	{
		_armRotate = armRotate;
		_armJoint = armJoint;
		_clawGrab = clawGrab;
	}
	
	/**
	 * change arm from one side to the other
	 */
	public void switchArmToOtherSide()
	{
		inverted = !inverted;
	}
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onArmJointJoyInput(double joyPower)
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
	
	/**
	 * Use joystick input to rotate arm
	 */
	public void onArmRotateJoyInput(double joyPower)
	{
		if(Math.abs(joyPower) >= .1)
		{
			_armRotate.setControlTarget((inverted ? 1 : -1) * joyPower);
		}
		else
		{
			_armRotate.setControlTarget(0);
		}
	}
	
	/**
	 * callback for when the claw joysticks are moved
	 * @param leftJoyPower
	 * @param rightJoyPower
	 */
	public void onClawJoyInput(double leftJoyPower, double rightJoyPower)
	{
		//if both joysticks are pushed in, close the claw
		if((leftJoyPower > .7) && (rightJoyPower < .7))
		{
			_clawGrab.startControl((leftJoyPower - .7) * (10.0/3.0));
		}
		else if((leftJoyPower < .7) && (rightJoyPower > .7))
		{
			_clawGrab.startControl((rightJoyPower - .7) * (10.0/3.0));
		}

	}
}
