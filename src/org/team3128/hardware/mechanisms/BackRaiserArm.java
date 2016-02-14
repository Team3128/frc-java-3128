package org.team3128.hardware.mechanisms;

import edu.wpi.first.wpilibj.CANTalon;

public class BackRaiserArm
{
	CANTalon armMotor;
	
	static final double GEAR_RATIO = 100.0/1;
	
	/**
	 * Get the angle of the arm in degrees.  Zero is entirely back, laying on the robot.
	 * @return
	 */
	public double getAngle()
	{
		return encDistanceToAngle((int)armMotor.getPosition());
	}
	
	public double encDistanceToAngle(int encoderDistance)
	{
		return (encoderDistance / 4.0) / GEAR_RATIO;
	}
}
