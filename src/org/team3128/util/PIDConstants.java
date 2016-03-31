package org.team3128.util;

/**
 * Structure to store PID constants
 * @author Jamie
 *
 */
public class PIDConstants
{
	public final double kP, kI, kD;
	
	public PIDConstants(double kP, double kI, double kD)
	{
		this.kP = kP;
		
		this.kI = kI;
		
		this.kD = kD;
	}
	
	public PIDConstants(double kP)
	{
		this.kP = kP;
		
		this.kI = 0;
		
		this.kD = 0;
	}
}