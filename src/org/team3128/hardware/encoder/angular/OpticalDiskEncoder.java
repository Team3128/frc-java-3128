package org.team3128.hardware.encoder.angular;

import edu.wpi.first.wpilibj.Encoder;


public class OpticalDiskEncoder implements IAngularEncoder
{
    private final Encoder enc;
   
    public OpticalDiskEncoder(Encoder e)
    {
    	this.enc = e;
    }
   
    public double getAngle()
    {
    	return enc.getRaw()/4.0;
    }
    
    public double getRawValue()
    {
    	return enc.getRaw();
    }

	@Override
	public boolean canRevolveMultipleTimes()
	{
		return true;
	}
}
