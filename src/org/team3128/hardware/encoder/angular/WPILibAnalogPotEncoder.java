package org.team3128.hardware.encoder.angular;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * Represents a variable resistor/potentiometer encoder using the WPILib AnalogPotentiometer class.
 * 
 * I can't believe I missed this before!
 * @author Jamie
 *
 */
public class WPILibAnalogPotEncoder implements IAngularEncoder
{
	AnalogInput input;
	
	AnalogPotentiometer potentiometer;
        
    /**
     * 
     * @param chan
     * @param off offset in degrees
     * @param travelLength the length in degrees of the travel
     */
    public WPILibAnalogPotEncoder(int chan, int off, double travelLength)
    {
    	input = new AnalogInput(chan);
    	
		potentiometer = new AnalogPotentiometer(input, travelLength, off);
	}
	
	@Override
	public double getAngle() {
		
		return potentiometer.get();
	}

	/**
	 * Get the voltage the potentiometer is producing
	 * @return
	 */
	@Override
	public double getRawValue()
	{
		return input.getVoltage();
	}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return false;
	}

}