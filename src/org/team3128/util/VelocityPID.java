package org.team3128.util;


public class VelocityPID
{
	
	double lastVelocityError = 0;
	
	double errorSum = 0;
	
	double Kp;
	
	double Ki;
	
	double Kd;
	
	double desiredVelocity;
	
	double storedVelocityAddition;
	
	public VelocityPID(double Pconstant, double Iconstant, double Dconstant)
	{
		Kp = Pconstant;
		Ki = Iconstant;
		Kd = Dconstant;
	}
	
	public void setDesiredVelocity(double velocity)
	{
		desiredVelocity = velocity;
	}
	
	/**
	 * Reset the internal sum of the error.
	 * This is a good thing to do if the robot has been sitting for some time and building up error.
	 */
	public void resetIntegral()
	{
		errorSum = 0;
	}
	
	/**
	 * Does another iteration of the PID control calculation
	 * @param currentVelocity
	 */
	public void update(double currentVelocity)
	{
		double error = desiredVelocity - currentVelocity;
		
		errorSum += error;
		
		double errorDelta = error - lastVelocityError;
		
		storedVelocityAddition = (Kp * error) + (Ki * errorSum) + (Kd * errorDelta);
		
		lastVelocityError = error;
	}
	
	/**
	 * 
	 * @return The number to add to the current control output to apply the PID correction.
	 * 
	 * This function does not do any calculating, you have to call update() first
	 */
	public double getOutputAddition()
	{
		return storedVelocityAddition;
	}
}
