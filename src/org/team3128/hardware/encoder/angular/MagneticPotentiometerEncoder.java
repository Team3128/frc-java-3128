package org.team3128.hardware.encoder.angular;

import edu.wpi.first.wpilibj.AnalogInput;


/**
 * Interfaces with magnetic angular encoders.
 * 
 * The ones we used in 2013-2014 are not very reliable, though,
 * so you may want to reconsider using them.
 * @author Jamie
 *
 */
public class MagneticPotentiometerEncoder implements IAngularEncoder
{
    private AnalogInput enc;
    private final double offset;
   
    public MagneticPotentiometerEncoder(int port)
    {
        enc = new AnalogInput(port);
        this.offset = 0;
    }
   
    public MagneticPotentiometerEncoder(double offset, int port)
    {
        enc = new AnalogInput(port);
        this.offset = offset;
    }
   
    /**
     * Gets the approximated angle from a magnetic encoder. It uses values which
     * have been estimated to high accuracy from extensive tests. Unless need be, 
     * do not modify these values.
     *
     * @return the approximate angle from 0 to 360 degrees of the encoder
     */
    @Override
    public double getAngle() 
    {
        double voltage = 0;
        
        for(char i = 0; i<10; i++)
        {
            voltage += enc.getVoltage();
        }
        
        voltage /= 10;
        return (voltage/5.0*360.0)+offset;
    }

    /**
     *
     * @return the raw voltage of the encoder
     */
    public double getRawValue() {return enc.getVoltage();}

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return true;
	}
}

