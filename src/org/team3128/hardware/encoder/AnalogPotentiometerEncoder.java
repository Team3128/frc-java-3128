package org.team3128.hardware.encoder;

import org.team3128.Log;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogPotentiometerEncoder implements IEncoder {
	private AnalogInput enc;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRawValue() {
		return enc.getVoltage();
	}
	
	public void printVal(){
		Log.debug("Encoder", "Pot: " + getRawValue());
	}

}
