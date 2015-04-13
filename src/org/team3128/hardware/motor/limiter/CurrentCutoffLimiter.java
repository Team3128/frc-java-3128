package org.team3128.hardware.motor.limiter;

import org.team3128.Log;
import org.team3128.hardware.motor.Limiter;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Motor control which moves the motor in a certain direction until its current spikes, which
 * (hopefully) means that it has hit the end of its travel.
 */

public class CurrentCutoffLimiter extends Limiter
{
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
    public CurrentCutoffLimiter(PowerDistributionPanel panel, int motorChannel, double currentThreshold, int refreshMillis)
    {
    	_panel = panel;
    	_motorChannel = motorChannel;
    	_currentThreshold = currentThreshold;
    }

    @Override
    public boolean canMove(double speed)
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

    @Override
    public void reset()
    {
    	consecutiveOvercurrents = 0;
    }

    
}

