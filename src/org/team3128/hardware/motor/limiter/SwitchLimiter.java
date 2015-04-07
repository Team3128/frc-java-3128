package org.team3128.hardware.motor.limiter;

import org.team3128.hardware.motor.Limiter;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control which uses an encoder to limit the range of something.  
 * 
 * It can also use current to decrease the power if necessary.
 * 
 * Note that this class assumes that running the motor backwards moves toward the minimum endstop.
 * @author Jamie
 *
 */
public class SwitchLimiter extends Limiter
{
    private DigitalInput _minSwitch, _maxSwitch;
    
    private boolean _activeLow;
        
    private boolean hitMinStop, hitMaxStop;
            
    /**
     * 
     * @param minSwitch limit switch which can be reached by setting the motor to reverse
     * @param maxSwitch limit switch which can be reached by setting the motor to go forward
     * @param activeLow whether the switches are active low
     */
    public SwitchLimiter(DigitalInput minSwitch, DigitalInput maxSwitch, boolean activeLow, PowerDistributionPanel panel, int channel, double maxAllowedCurrent)
    {
    	_minSwitch = minSwitch;
    	_maxSwitch = maxSwitch;
    	
    	_activeLow = activeLow;
    }

	@Override
	public boolean canMove(double power)
	{
		hitMinStop = _minSwitch.get() != _activeLow;
    	
    	hitMaxStop = _maxSwitch.get() != _activeLow;
    	
    	//Log.debug("LimitSwitchEndstop", "hitMinStop: " + hitMinStop + " hitMaxStop: " + hitMaxStop);
    	
    	if(hitMinStop && power < 0)
    	{
    		return false;
    	}
    	else if(hitMaxStop && power > 0)
    	{
    		return false;
    	}
    	return true;
    	
	}
    
}

