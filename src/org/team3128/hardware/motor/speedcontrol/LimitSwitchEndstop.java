package org.team3128.hardware.motor.speedcontrol;

import org.team3128.Log;
import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control which uses an encoder to limit the range of something.  
 * 
 * It can also use current to decrease the poerr if neccesary.
 * 
 * Note that this class assumes that running the motor backwards moves toward the minimum endstop.
 * @author Jamie
 *
 */
public class LimitSwitchEndstop extends MotorControl
{
    private DigitalInput _minSwitch, _maxSwitch;
    
    private boolean _activeLow;
    
    private double targetSpeed;
    
    private boolean hitMinStop, hitMaxStop;
    
    private PowerDistributionPanel _panel;
    
    private int pdpChannel;
    
    private double _maxAllowedCurrent;

    /**
     * 
     * @param minSwitch limit switch which can be reached by setting the motor to reverse
     * @param maxSwitch limit switch which can be reached by setting the motor to go forward
     * @param activeLow whether the switches are active low
     * @param maxAllowedCurrent maximum current that can pass through the channel before the power should be decreased.
     */
    public LimitSwitchEndstop(DigitalInput minSwitch, DigitalInput maxSwitch, boolean activeLow, PowerDistributionPanel panel, int channel, double maxAllowedCurrent)
    {
    	_minSwitch = minSwitch;
    	_maxSwitch = maxSwitch;
    	
    	_activeLow = activeLow;
    	
    	_panel = panel;
    	
    	pdpChannel = channel;
    	
    	_refreshTime = 100;
    	
    	_maxAllowedCurrent = maxAllowedCurrent;
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
    	
    	if(_maxAllowedCurrent > 0 && _panel.getCurrent(pdpChannel) > _maxAllowedCurrent)
    	{
    		Log.debug("LimitSwitchEndstop", Double.toString(targetSpeed));
    		targetSpeed -= RobotMath.sgn(targetSpeed) * .04;
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

