package org.team3128.hardware.motor.speedcontrol;

import org.team3128.Log;
import org.team3128.hardware.encoder.velocity.IVelocityEncoder;
import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;
import org.team3128.util.VelocityPID;

/*        _
 *       / \ 
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
 * Speed control that uses PID to hit its target speed more accurately.
 *
 * @author Jamie
 */
public class PIDSpeedTarget extends MotorControl
{   
    private IVelocityEncoder _encoder;
    
    private VelocityPID _pidCalculator;
    
    /**
     * Target speed in RPM.
     */
    protected double _targetSpeed;

    /**
     *
     * @param tgtSpeed    target speed in rpm
     * @param refreshTime speed update rate in msec
     * @param encoder the encoder to use
     * @param kP the Konstant of Proportional
     * @param kI the Konstant of Integral
     * @param kD the Konstant of Derivative
     */
    public PIDSpeedTarget(double tgtSpeed, int refreshTime, IVelocityEncoder encoder, double kP, double kI, double kD)
    {
    	_targetSpeed = tgtSpeed;
        _refreshTime = refreshTime;
        _encoder = encoder;
        _pidCalculator = new VelocityPID(kP, kI, kD);
        _pidCalculator.setDesiredVelocity(tgtSpeed);
    }
   
    /**
     * Uses a default refreshTime of 50msec
     *
     * @param tgtSpeed target speed in rpm
     */
    public PIDSpeedTarget(double tgtSpeed, IVelocityEncoder encoder, VelocityPID pidCalc)
    {
        _encoder = encoder;
        _pidCalculator = pidCalc;
    }
   
    public void setControlTarget(double d)
    {
        targetLock.lock();
        _pidCalculator.resetIntegral();
        _targetSpeed = d;
    	_pidCalculator.setDesiredVelocity(d);
    	
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
    	
    	double speed = -1 * _encoder.getSpeedInRPM();
    	if(Math.abs(speed) < 5.0)
    	{
    		speed = 0;
    	}
        _pidCalculator.update(speed);
        
        double output = RobotMath.makeValidPower(RobotMath.getEstMotorPowerForRPM(_targetSpeed + _pidCalculator.getOutputAddition()));
        
        Log.debug("PIDSpeedTarget", "Current RPM: " + speed + " Output: " + output);
        
        
        return output;
    }

    /**
     * Sets the speed update time in msec
     *
     * @param refreshTime speed update rate in msec
     */
    public void setRefreshTime(int refreshTime)
    {
    	targetLock.lock();
    	super._refreshTime = refreshTime;
    	targetLock.unlock();
    }
   
    public void clearControlRun()
    {
        setControlTarget(0);
        _pidCalculator.resetIntegral();
    }

    public boolean isComplete()
    {
    	return false;
    }
}
