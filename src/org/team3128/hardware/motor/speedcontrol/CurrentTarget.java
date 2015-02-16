package org.team3128.hardware.motor.speedcontrol;

import org.team3128.Log;
import org.team3128.hardware.motor.MotorControl;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control which moves the motor in a certain direction until its current spikes, which
 * (hopefully) means that it has hit the end of its travel
 */

public class CurrentTarget extends MotorControl
{
    private double speed;
    private int _motorChannel;
    private PowerDistributionPanel _panel;
    
    private double _currentThreshold;
    
    private int consecutiveOvercurrents = 0;

    /**
     * 
     * @param panel PDP object to use
     * @param motorChannel channel motor controller is plugged into on PDP
     * @param currentThreshold measured amperage where motor is considered stalled.  You have to measure this
     * for each individual motor.
     * @param refreshMillis
     */
    public CurrentTarget(PowerDistributionPanel panel, int motorChannel, double currentThreshold, int refreshMillis)
    {
    	_panel = panel;
    	_motorChannel = motorChannel;
    	_currentThreshold = currentThreshold;
    	
    	_refreshTime = refreshMillis;
    }

    /**
     * sets speed and direction of travel
     */
    public void setControlTarget(double val)
    {
        targetLock.lock();
        this.speed = val;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
        return speed;
    }

    public void clearControlRun() {}

    public boolean isComplete()
    {
        Log.debug("CurrentTarget", "motor current: " + _panel.getCurrent(_motorChannel));
        if(_panel.getCurrent(_motorChannel) > _currentThreshold)
        {
        	++consecutiveOvercurrents;
        }
        else
        {
        	consecutiveOvercurrents = 0;
        }
        
        if(consecutiveOvercurrents >= 3)
        {
        	return true;
        }
        return false;
    }
    
}

