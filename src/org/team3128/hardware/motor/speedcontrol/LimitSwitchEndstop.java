package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.motor.MotorControl;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Motor control which uses an encoder to limit the range of something.  
 * 
 * Note that this class assumes that running the motor backwards 
 * decreases the encoder angle.
 * @author Jamie
 *
 */
public class LimitSwitchEndstop extends MotorControl
{
    private DigitalInput _minSwitch, _maxSwitch;
    
    private boolean _activeLow;
    
    private double targetSpeed;
    
    private boolean hitMinStop, hitMaxStop;

    /**
     * 
     * @param minSwitch limit switch which can be reached by setting the motor to reverse
     * @param maxSwitch limit switch which can be reached by setting the motor to go forward
     * @param activeLow whether the switches are active low
     */
    public LimitSwitchEndstop(DigitalInput minSwitch, DigitalInput maxSwitch, boolean activeLow)
    {
    	_minSwitch = minSwitch;
    	_maxSwitch = maxSwitch;
    	
    	_activeLow = activeLow;
    }

    /**
     * sets motor speed
     */
    public void setControlTarget(double val)
    {
        targetLock.lock();
        targetSpeed = val;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
    	
    	hitMinStop = _minSwitch.get() != _activeLow;
    	
    	hitMaxStop = _maxSwitch.get() != _activeLow;
    	
    	//Log.debug("LimitSwitchEndstop", "hitMinStop: " + hitMinStop + " hitMaxStop: " + hitMaxStop);
    	
    	if(hitMinStop && targetSpeed < 0)
    	{
    		return 0;
    	}
    	else if(hitMaxStop && targetSpeed > 0)
    	{
    		return 0;
    	}
    	
        return targetSpeed;
    }

    public void clearControlRun()
    {
    	targetSpeed = 0;
    }

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {
        return false;
    }
    
}

