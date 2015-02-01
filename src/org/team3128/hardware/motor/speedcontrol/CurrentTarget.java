package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.motor.MotorControl;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control which moves the motor in a certain direction until its current soikes, which
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
    */
    public CurrentTarget(PowerDistributionPanel panel, int motorChannel, double currentThreshold)
    {
    	_panel = panel;
    	_motorChannel = motorChannel;
    	_currentThreshold = currentThreshold;
    	
    	_refreshTime = 100;
    }

    /**
     * sets degree value to move to
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

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {
        //Log.debug("CurrentTarget", "motor current: " + _panel.getCurrent(_motorChannel));
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

