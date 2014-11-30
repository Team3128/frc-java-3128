package org.team3128.hardware.motor.speedcontrol;

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

    public LinearAngleTarget(double minSpeed, double threshold) {
        if (!RobotMath.isValidPower(minSpeed)) {
            throw new IllegalArgumentException("The minimum power is incorrect!");
        }
        this.minSpeed = Math.abs(minSpeed);
        this.threshold = threshold;
    }

    public void setControlTarget(double val)
    {
        targetLock.lock();
        this.targetAngle = (val % 180 == 0 ? this.targetAngle : val);
        targetLock.unlock();
    }

    public double speedControlStep(double dt)
    {
        double error = RobotMath.angleDistance(this.getLinkedEncoderAngle(), this.targetAngle);
        double sgn = RobotMath.sgn(error);
        double pGain = sgn*(Math.abs(error))*((1-this.minSpeed)/90.0)+this.minSpeed;
        pGain = (Math.abs(pGain) > this.minSpeed ? pGain : RobotMath.getMotorDirToTarget(this.getLinkedEncoderAngle(), this.targetAngle).getIntDir() * this.minSpeed);
       
        if(Math.abs(error) < threshold) {return 0;}
        return pGain;
    }

    public void clearControlRun() {}

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {
        double x =  Math.abs(RobotMath.angleDistance(this.getLinkedEncoderAngle(), this.targetAngle));
        return x < threshold;
    }
    
}

