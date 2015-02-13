package org.team3128.hardware.encoder.angular;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogPotentiometerEncoder implements IAngularEncoder {
	private AnalogInput enc;
	private double degreesPerVolt;
    private final double offset;
    
    /**
     * 
     * @param chan
     * @param off offset in degrees
     * @param voltsAtEndOfTravel the voltage when the encoder is at the end of its travel
     * @param travelLength the length in degrees of the travel
     */
    public AnalogPotentiometerEncoder(int chan, int off, double voltsAtEndOfTravel, double travelLength)
    {
		enc = new AnalogInput(chan);
		offset = off;
		
		degreesPerVolt = travelLength / voltsAtEndOfTravel;
	}
	
	@Override
	public double getAngle() {
		
		return (getRawValue() * degreesPerVolt) + offset;
	}

	@Override
	public double getRawValue() {
		return enc.getVoltage();
	}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return false;
	}

}
