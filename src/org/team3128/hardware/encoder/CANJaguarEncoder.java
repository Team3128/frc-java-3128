package org.team3128.hardware.encoder;

import org.team3128.Log;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.encoder.velocity.IVelocityEncoder;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CANJaguar.ControlMode;

/*        _
 *       / \ 
 *      / _ \
 *     / [ ] \
 *    /  [_]  \
 *   /    _    \
 *  /    (_)    \
 * /_____________\
 * -----------------------------------------------------
 * UNTESTED CODE!
 * This class has never been tried on an actual robot.
 * It may be non or partially functional.
 * Do not make any assumptions as to its behavior!
 * And don't blink.  Not even for a second.
 * -----------------------------------------------------*/

/**
 * Encoder wrapper class for the CAN Jaguar.
 * 
 * Make sure to set the control mode and position reference on the jaguar first!
 * @author Jamie
 *
 */
public class CANJaguarEncoder implements IAngularEncoder, IVelocityEncoder
{
	private CANJaguar jaguar;
	
	private boolean canRevolveMultipleTimes;
	
	/**
	 * 
	 * @param jaguar
	 * @param canRevolveMultipleTimes whether the motor can complete multiple 360 degree turns without issue
	 */
	public CANJaguarEncoder(CANJaguar jaguar, boolean canRevolveMultipleTimes)
	{
		this.jaguar = jaguar;
		this.canRevolveMultipleTimes = canRevolveMultipleTimes;
	}


	private void assertControlMode(CANJaguar.ControlMode mode)
	{
		if(jaguar.getControlMode() != mode)
		{
			Log.fatal("CANJaguarEncoder", "Cannot take measurement, the jaguar is not in " + mode.toString().toLowerCase()+ " mode");
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public double getSpeedInRPM()
	{
		assertControlMode(ControlMode.Speed);
		return jaguar.getSpeed();
	}

	@Override
	public double getAngle()
	{
		return getRawValue() * 360;
	}

	@Override
	public double getRawValue()
	{
		assertControlMode(ControlMode.Position);
		return jaguar.getPosition();
	}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return canRevolveMultipleTimes;
	}

}
