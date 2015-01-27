package org.team3128.hardware.motor.speedcontrol;

import org.team3128.Log;
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
    
    protected double calculatedSpeed;

    /**
     *
     * @param tgtSpeed    target speed in rpm
     * @param refreshTime speed update rate in msec
     */
    public PIDSpeedTarget(double tgtSpeed, int refreshTime, IVelocityEncoder encoder, double kP, double kI, double kD)
    {
        this.tgtSpeed = tgtSpeed;
        calculatedSpeed = tgtSpeed;
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
        this.tgtSpeed = tgtSpeed;
        _encoder = encoder;
        _pidCalculator = pidCalc;
    }
   
    public void setControlTarget(double d)
    {
        targetLock.lock();
    	tgtSpeed = d;
    	calculatedSpeed = d;
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
        
        
        Log.debug("PIDSpeedTarget", "Target: " + tgtSpeed + " Speed: " + speed + " Current: " + calculatedSpeed + " Output: " + RobotMath.getEstMotorPowerForRPM(calculatedSpeed + _pidCalculator.getOutputAddition()));
        
        calculatedSpeed += _pidCalculator.getOutputAddition();
        
        return RobotMath.makeValidPower(RobotMath.getEstMotorPowerForRPM(calculatedSpeed));
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
    }

    public boolean isComplete()
    {
    	return false;
    }
}
