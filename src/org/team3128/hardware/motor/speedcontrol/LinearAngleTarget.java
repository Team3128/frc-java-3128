package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;

/**
 * Motor control which steers the motor to an angle using an encoder.
 * @author Yousuf Soliman
 */

public class LinearAngleTarget extends MotorControl
{
    private double minSpeed;
    private double targetAngle, threshold;
    private IAngularEncoder _encoder;

    /**
     * 
     * @param minSpeed
     * @param threshold acceptable error in degrees
     * @param encoder
     */
    public LinearAngleTarget(double minSpeed, double threshold, IAngularEncoder encoder)
    {
        if (!RobotMath.isValidPower(minSpeed))
        {
            throw new IllegalArgumentException("The minimum power is incorrect!");
        }
        
        this.minSpeed = Math.abs(minSpeed);
        this.threshold = threshold;
        _encoder = encoder;
    }

    /**
     * sets degree value to move to
     */
    public void setControlTarget(double val)
    {
        targetLock.lock();
        this.targetAngle = val;
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
    	double angle = _encoder.getAngle();
        double error = RobotMath.angleDistance(angle, this.targetAngle);
        double sgn = RobotMath.sgn(error);
        double pGain = sgn*(Math.abs(error))*((1-this.minSpeed)/90.0)+this.minSpeed;
        pGain = (Math.abs(pGain) > this.minSpeed ? pGain : RobotMath.getMotorDirToTarget(angle, this.targetAngle).getIntDir() * this.minSpeed);
       
        if(Math.abs(error) < threshold)
        {
        	return 0;
        }
        
        return pGain;
    }

    public void clearControlRun() {}

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {
        double x =  Math.abs(RobotMath.angleDistance(_encoder.getAngle(), this.targetAngle));
        return x < threshold;
    }
    
}

