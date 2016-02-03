package org.team3128.hardware.encoder.velocity;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Driver for a CTRE Magnetic Encoder using DIO ports on the roborio.
 * 
 * When instantiated, it sets the quadrature distance from the absolute angle.
 * So, between 0 and 1 rotations. When reset, the distance goes to zero.
 * 
 * Internally, it uses a Counter to measure the PWM and a WPILib Encoder object
 * to measure the quadrature part.
 * 
 * @author Narwhal
 *
 */
public class CTREMagneticEncoder implements IVelocityEncoder, IDistanceEncoder,
		IAngularEncoder
{
	// had to get this from a forum post by a CTR employee
	private static final int PULSES_PER_REVOLUTION = 1024;

	Encoder encoder;
	Counter pwmCounter;

	double offsetDegrees;

	/**
	 * 
	 * @param dataAPort
	 *            DIO port with the "A" data line plugged in (pin 7 on the
	 *            encoder)
	 * @param dataBPort
	 *            DIO port with the "B" data line plugged in to it (pin 5 on the
	 *            encoder)
	 * @param pwmPort
	 *            DIO port connected to pin 9 on the encoder, the PWM pin
	 * 
	 * @param inverted
	 *            whether or not the encoder is inverted
	 */
	public CTREMagneticEncoder(int dataAPort, int dataBPort, int pwmPort, double pulsesPerRevolution, boolean inverted) 
	{
		encoder = new Encoder(dataAPort, dataBPort);
		encoder.setDistancePerPulse(1/pulsesPerRevolution);
		
		pwmCounter = new Counter(pwmPort);
		pwmCounter.setSemiPeriodMode(true); //only count rising edges
		
		//wait for the pwm signal to be counted
		try
		{
			Thread.sleep(5);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
		offsetDegrees = getAngle();
		
	}

	@Override
	public double getSpeedInRPM()
	{

		// getRate returns rotations / second
		return encoder.getRate() * 60;
	}

	public void clear()
	{
		encoder.reset();
		offsetDegrees = 0;
	}

	@Override
	public double getDistanceInDegrees()
	{
		return encoder.getDistance() + (offsetDegrees / 360);
	}

	@Override
	public double getAngle()
	{
		//from 1 to 4096 us
		return (pwmCounter.getPeriod() - 1e-6) / 4095e-6;
	}

	@Override
	public double getRawValue()
	{
		return pwmCounter.getPeriod();
	}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return true;
	}

}
