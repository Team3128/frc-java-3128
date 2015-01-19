package org.team3128.hardware.encoder;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogPotentiometerEncoder implements IEncoder {
	private AnalogInput enc;
	private final double constToDeg = 2168.470588235294;
    private final double offset;
    public AnalogPotentiometerEncoder(int chan){
		enc = new AnalogInput(chan);
		offset = 0;
	}
    
    public AnalogPotentiometerEncoder(int chan, int off){
		enc = new AnalogInput(chan);
		offset = off;
	}
	
	@Override
	public double getAngle() {
		
		return (getRawValue() * constToDeg) + offset;
	}

	@Override
	public double getRawValue() {
		return enc.getVoltage();
	}

}
