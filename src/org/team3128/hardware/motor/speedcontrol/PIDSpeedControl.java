package org.team3128.hardware.motor.speedcontrol;

import org.team3128.Log;
import org.team3128.hardware.encoder.velocity.IVelocityEncoder;
import org.team3128.hardware.motor.SpeedControl;
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
public class PIDSpeedControl extends SpeedControl
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
    public PIDSpeedControl(double tgtSpeed, int refreshTime, IVelocityEncoder encoder, double kP, double kI, double kD)
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
    public PIDSpeedControl(double tgtSpeed, IVelocityEncoder encoder, VelocityPID pidCalc)
    {
        _encoder = encoder;
        _pidCalculator = pidCalc;
    }
   
    @Override
    public synchronized void setControlTarget(double d)
    {
        _pidCalculator.resetIntegral();
        _targetSpeed = d;
    	_pidCalculator.setDesiredVelocity(d);
    }

    @Override
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
   
    @Override
    public synchronized void clearControlRun()
    {
        setControlTarget(0);
        _pidCalculator.resetIntegral();
    }

    @Override
    public boolean isComplete()
    {
    	return false;
    }
}
