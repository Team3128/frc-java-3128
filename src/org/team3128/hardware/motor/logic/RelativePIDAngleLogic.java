package org.team3128.hardware.motor.logic;

import org.team3128.Log;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorLogic;
import org.team3128.util.RobotMath;

/**
 * Motor control which steers the motor to an angle using an encoder.
 * @author Yousuf Soliman
 * @author Jamie
 */

public class RelativePIDAngleLogic extends MotorLogic
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
    
    boolean _log;
    
    final static double errorLimit = 100000;
    
    /**
     * 
     * @param kP constant of pid
     * @param threshold acceptable error in degrees
     * @param stopWhenDone whether to stop controlling the motor when it's reached its target
     * @param encoder
     */
    public RelativePIDAngleLogic(double kP, double kI, double kD, double threshold, boolean stopWhenDone, IAngularEncoder encoder, boolean log)
    {
    	_refreshTime = 10;
        
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        
        _log = log;
        
        this.threshold = threshold;
        _encoder = encoder;
        
        _stopWhenDone = stopWhenDone;
    }

    /**
     * sets degree value to move to
     */
    @Override
    public synchronized void setControlTarget(double val)
    {
        this.targetAngle = val;
        
        //reset error
        errorSum = 0;
    }

    @Override
    public double speedControlStep(double dt)
    {
    	double angle = _encoder.getAngle();
    	
    	double error = RobotMath.angleDistance(angle, this.targetAngle, _encoder.canRevolveMultipleTimes());
    	    	
    	errorSum += error;
    	
    	if(errorSum > errorLimit)
    	{
    		Log.unusual("PIDAngleTarget", "I error sum of " + errorSum + " went over limit of " + errorLimit);
    		//errorSum = errorLimit;
    	}
    	else if(errorSum < -errorLimit)
    	{
    		Log.unusual("PIDAngleTarget", "I error sum of " + errorSum + " went under limit of " + -errorLimit);
    		//errorSum = -errorLimit;
    	}
    	
        double output = error * kP + errorSum * kI + kD * (error - prevError);
        
        prevError = error;
        
       	if(_log)
    	{
            Log.debug("PIDAngleTarget", "target: " + targetAngle + " current: " + angle + " error: " + error + " output: " + output);
    	}

        if(Math.abs(error) < threshold)
        {
        	++consecutiveCorrectPositions;
        	return 0;
        }
        consecutiveCorrectPositions = 0;
        
        return RobotMath.makeValidPower(output);
    }

    @Override
    public synchronized void clearControlRun()
    {
    	errorSum = 0;
    	consecutiveCorrectPositions = 0;
    }

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {	
        return _stopWhenDone && consecutiveCorrectPositions >= 5;
    }
    
}

