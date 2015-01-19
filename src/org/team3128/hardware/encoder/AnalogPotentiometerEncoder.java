package org.team3128.hardware.encoder;

import org.team3128.Log;
import org.team3128.listener.IListenerCallback;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogPotentiometerEncoder implements IEncoder {
	private AnalogInput enc;
	private final double constToDeg = 2168.470588235294;
    private final double offset;
    private double p1;
	private double p2;
	public IListenerCallback printPot = () -> {
		Log.debug("PotTest", "" + Math.abs(p2-p1));
		p2 = getAngle();
	};
	public IListenerCallback zeroPot = () -> {
		p1 = getAngle();
	};
    public AnalogPotentiometerEncoder(int chan){
		enc = new AnalogInput(chan);
		p1 = getAngle();
		p2 = getAngle();
		offset = 0;
	}
    
    public AnalogPotentiometerEncoder(int chan, int off){
		enc = new AnalogInput(chan);
		p1 = getRawValue();
		p2 = getRawValue();
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
