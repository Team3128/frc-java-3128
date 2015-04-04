package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.encoder.velocity.IVelocityEncoder;
import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;

/**
 * Used for computing a target power with a linear shift.
 *
 * @author Noah Sutton-Smolin
 */
public class LinearSpeedTarget extends MotorControl
{
    private double tgtSpeed;
    
    private IVelocityEncoder _encoder;

    /**
     *
     * @param tgtSpeed    target speed in rpm
     * @param refreshTime speed update rate in msec
     */
    public LinearSpeedTarget(double tgtSpeed, int refreshTime, IVelocityEncoder encoder)
    {
        this.tgtSpeed = tgtSpeed;
        _refreshTime = refreshTime;
        _encoder = encoder;
    }
   
    /**
     * Uses a default refreshTime of 50msec
     *
     * @param tgtSpeed target speed in rpm
     */
    public LinearSpeedTarget(double tgtSpeed, IVelocityEncoder encoder)
    {
        this.tgtSpeed = tgtSpeed;
        _encoder = encoder;
    }
   
    public void setControlTarget(double d)
    {
        targetLock.lock();
    	tgtSpeed = d;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
        double speed = _encoder.getSpeedInRPM();

        return RobotMath.makeValidPower(tgtSpeed * (speed / RobotMath.getMotorExpectedRPM(tgtSpeed)));
    }

    /**
     * Sets the speed update time in msec
     *
     * @param refreshTime time between updates in msec
     */
    public void setRefreshTime(int refreshTime)
    {
    	targetLock.lock();
    	super._refreshTime = refreshTime;
    	targetLock.unlock();
    }
   
    public void clearControlRun()
    {
        targetLock.lock();
    	this.tgtSpeed = 0;
        targetLock.unlock();
    }

    public boolean isComplete()
    {
    	return false;
    }
}
