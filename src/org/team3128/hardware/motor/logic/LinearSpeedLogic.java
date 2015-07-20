package org.team3128.hardware.motor.logic;

import org.team3128.hardware.encoder.velocity.IVelocityEncoder;
import org.team3128.hardware.motor.MotorLogic;
import org.team3128.util.RobotMath;

/**
 * Used for computing a target power with a linear shift.
 *
 * @author Noah Sutton-Smolin
 */
public class LinearSpeedLogic extends MotorLogic
{
    private double tgtSpeed;
    
    private IVelocityEncoder _encoder;

    /**
     *
     * @param tgtSpeed    target speed in rpm
     * @param refreshTime speed update rate in msec
     */
    public LinearSpeedLogic(double tgtSpeed, int refreshTime, IVelocityEncoder encoder)
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
    public LinearSpeedLogic(double tgtSpeed, IVelocityEncoder encoder)
    {
        this.tgtSpeed = tgtSpeed;
        _encoder = encoder;
    }
   
    @Override
    public synchronized void setControlTarget(double d)
    {
    	tgtSpeed = d;
    }

    @Override
    public double speedControlStep(double dt)
    {
        double speed = _encoder.getSpeedInRPM();

        return RobotMath.makeValidPower(tgtSpeed * (speed / RobotMath.getCIMExpectedRPM(tgtSpeed)));
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
