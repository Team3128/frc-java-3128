package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.encoder.velocity.IVelocityEncoder;
import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;
import org.team3128.util.VelocityPID;

/**
 * Used for computing a target power with a linear shift.
 *
 * @author Noah Sutton-Smolin
 */
public class PIDSpeedTarget extends MotorControl
{
    private double tgtSpeed;
    
    private IVelocityEncoder _encoder;
    
    private VelocityPID _pidCalculator;

    /**
     *
     * @param tgtSpeed    target speed in rpm
     * @param refreshTime speed update rate in msec
     */
    public PIDSpeedTarget(double tgtSpeed, int refreshTime, IVelocityEncoder encoder, double kP, double kI, double kD)
    {
        this.tgtSpeed = tgtSpeed;
        _refreshTime = refreshTime;
        _encoder = encoder;
        _pidCalculator = new VelocityPID(kP, kI, kD);
        _pidCalculator.setDesiredVelocity(RobotMath.getMotorExpectedRPM(tgtSpeed));
    }
   
    /**
     * Uses a default refreshTime of 50msec
     *
     * @param tgtSpeed target speed in rpm
     */
    public PIDSpeedTarget(double tgtSpeed, IVelocityEncoder encoder, VelocityPID pidCalc)
    {
        this.tgtSpeed = tgtSpeed;
        _encoder = encoder;
        _pidCalculator = pidCalc;
    }
   
    public void setControlTarget(double d)
    {
        targetLock.lock();
    	tgtSpeed = d;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
        _pidCalculator.update(_encoder.getSpeedInRPM());

        return RobotMath.makeValidPower(tgtSpeed + _pidCalculator.getOutputAddition());
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
        targetLock.lock();
    	this.tgtSpeed = 0;
        targetLock.unlock();
    }

    public boolean isComplete()
    {
    	return false;
    }
}
