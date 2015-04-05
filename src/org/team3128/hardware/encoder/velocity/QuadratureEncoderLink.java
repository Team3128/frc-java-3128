package org.team3128.hardware.encoder.velocity;

import org.team3128.hardware.encoder.distance.IDistanceEncoder;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Class that uses a quadrature encoder via WPIlib and the FPGA to measure speed.
 * @author Narwhal
 *
 */
public class QuadratureEncoderLink implements IVelocityEncoder, IDistanceEncoder
{
	Encoder encoder;
	
	/**
	 * 
	 * @param dataAPort DIO port with the "A" data line plugged in to it
	 * @param dataBPort DIO port with the "B" data line plugged in to it
	 * @param pulsesPerRevolution The pulses per revolution of the encoder.  It should say on the encoder
	 * or its datasheet.
	 * @param inverted whether or not the encoder is inverted
	 */
	public QuadratureEncoderLink(int dataAPort, int dataBPort, double pulsesPerRevolution, boolean inverted) 
	{
		encoder = new Encoder(dataAPort, dataBPort);
		encoder.setDistancePerPulse(1/pulsesPerRevolution);
	}

	@Override
	public double getSpeedInRPM() {
		
		//getRate returns rotations / second
		return encoder.getRate() * 60;
	}
	
	/**
	 * Not (yet) supported
	 */
	public void clear()
	{
		encoder.reset();
	}

	@Override
	public double getDistanceInDegrees()
	{
		return encoder.getDistance();
	}

}
