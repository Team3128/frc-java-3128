package org.team3128.hardware.motor.logic;

import org.team3128.hardware.motor.MotorLogic;

/**
 * This class implements the abstract functions in MotorLogic, but simply mirrors having no speed control at all.
 * It is used as a base for making other speed controls as well as in the case where we need a limiter but no speed control.
 * @author Jamie
 *
 */
public class BlankSpeedLogic extends MotorLogic
{
    private double tgtSpeed;
    
    /**
     * Construct BlankSpeedLogic
     * @param tgtSpeed    target speed in motor power
     */
    public BlankSpeedLogic(double tgtSpeed)
    {
        this.tgtSpeed = tgtSpeed;
    }
   
    @Override
    public synchronized void setControlTarget(double d)
    {
    	tgtSpeed = d;
    }

    @Override
    public double speedControlStep(double dt)
    {
        return tgtSpeed;
    }
   
    @Override
    public synchronized void clearControlRun()
    {
    	this.tgtSpeed = 0;
    }

    @Override
    public boolean isComplete()
    {
    	return false;
    }
}
