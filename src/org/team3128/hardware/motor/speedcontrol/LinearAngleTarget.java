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
    private double targetAngle, threshold;
    private IAngularEncoder _encoder;
    
    private int consecutiveCorrectPositions = 0;
    
    boolean _stopWhenDone;
    double kP;
    double kI;
    double kD;
    
    double errorSum = 0;
    double prevError = 0;
    
    /**
     * 
     * @param kP constant of pid
     * @param threshold acceptable error in degrees
     * @param stopWhenDone whether to stop controlling the motor when it's reached its target
     * @param encoder
     */
    public LinearAngleTarget(double kP, double kI, double kD, double threshold, boolean stopWhenDone, IAngularEncoder encoder)
    {
    	_refreshTime = 10;
        
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        
        this.threshold = threshold;
        _encoder = encoder;
        
        _stopWhenDone = stopWhenDone;
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
    	
    	double error = RobotMath.angleDistance(angle, this.targetAngle, _encoder.canRevolveMultipleTimes());
    	
    	errorSum += error;
    	
        double output = error * kP + errorSum * kI + kD * (error - prevError);
        
        prevError = error;
        
        //Log.debug("LinearAngleTarget", "target: " + targetAngle + " current: " + angle + " error: " + error + " output: " + (pGain));
        
        //if(Math.abs(pGain) <= this.minSpeed)
        //{
        //	pGain = RobotMath.getMotorDirToTarget(angle, this.targetAngle, _encoder.canRevolveMultipleTimes()).getIntDir() * this.minSpeed;
        //}

        if(Math.abs(error) < threshold)
        {
        	++consecutiveCorrectPositions;
        	return 0;
        }
        consecutiveCorrectPositions = 0;
        
        return RobotMath.makeValidPower(output);
    }

    public void clearControlRun()
    {
    	 consecutiveCorrectPositions = 0;
    }

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {
    	
        return _stopWhenDone & consecutiveCorrectPositions >= 5;
    }
    
}

