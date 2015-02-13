package org.team3128.hardware.encoder.angular;

public interface IAngularEncoder
{
    public double getAngle();
    public double getRawValue();
    
    /**
     * 
     * @return true if the encoder can revolve multiple times in the same direction, 
     * e.g. a magnetic encoder
     */
    public boolean canRevolveMultipleTimes();
}

