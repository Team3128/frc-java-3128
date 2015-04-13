package org.team3128.hardware.motor;

/**
 * Limiters are classes which limit a MotorLogic's behavior.
 * For example, you might want to limit the current a thing draws, but also make it stop when it hits an endstop.
 * Before limiters, this wasn't possible without manually merging the speed control classes.
 * @author Jamie
 *
 */
public abstract class Limiter
{
	/**
	 * 
	 * @param power the power being set for the motor.
	 * @return true if it is OK for the motor to move at the given power. Otherwise, its power will be set to 0.
	 */
	public abstract boolean canMove(double power);
	
	/**
	 * Reset any permanent state the the limiter has.
	 */
	public void reset(){}
}
