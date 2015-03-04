package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorControl;

/*
 *       /^\ 
 *      / _ \
 *     / [ ] \
 *    /  [_]  \
 *   /    _    \
 *  /    (_)    \
 * /_____________\
 * -----------------------------------------------------
 * UNTESTED CODE!
 * This class has never been tried on an actual robot.
 * It may be non or partially functional.
 * Do not make any assumptions as to its behavior!
 * And don't blink.  Not even for a second.
 * -----------------------------------------------------*/

/**
 * Motor control which uses an encoder to limit the range of something.  
 * 
 * Note that this class assumes that running the motor backwards 
 * decreases the encoder angle.
 * @author Jamie
 *
 */
public class AngleEndstopTarget extends MotorControl
{
    private double _minAngle, _maxAngle;
    
    private double _jitter;
    
    private boolean hitMinStop, hitMaxStop;
    
    private double targetSpeed;
    
    private IAngularEncoder _encoder;

    /**
     * 
     * @param minAngle The angle that the encoder should not go lower than
     * @param maxAngle The angle that the encoder should not go higher than
     * @param jitter How many degrees forward to "proactively" stop the mechanism, used to give
     * the thing time to stop.
     * @param encoder The encoder to use.
     */
    public AngleEndstopTarget(double minAngle, double maxAngle, double jitter, IAngularEncoder encoder)
    {
    	_minAngle = minAngle;
    	_maxAngle = maxAngle;
    	_jitter = jitter;
        _encoder = encoder;
    }

    /**
     * sets speed
     */
    public void setControlTarget(double val)
    {
        targetLock.lock();
        targetSpeed = val;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
    	hitMinStop = _encoder.getAngle() < (_minAngle + _jitter);
    	
    	hitMaxStop = _encoder.getAngle() > (_maxAngle - _jitter);
    	
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

