package org.team3128.util;

/**
 * This is a simple class which represents the motor direction.
 * @author Jamie
 *
 */
public enum MotorDir
{
	CCW(-1),
	NONE(0),
	CW(1);
	
	int integerDirection;
	
	MotorDir(int integerDirection)
	{
		this.integerDirection = integerDirection;
	}
	
	/**
	 * 
	 * @return The integer constant to this direction
	 */
	public int getIntDir()
	{
		return integerDirection;
	}
}
