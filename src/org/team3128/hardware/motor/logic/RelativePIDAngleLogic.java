package org.team3128.hardware.motor.logic;

import org.team3128.Log;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorLogic;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Motor control which steers the motor to an angle using an encoder. 
 * 
 * This class uses a relative encoder, like an optical or magnetic one that counts revolutions,
 *  as opposed to an absolute one like a potentiometer.
 *  
 * @author Yousuf Soliman
 * @author Jamie
 */

public class RelativePIDAngleLogic extends MotorLogic
{
    private double targetAngle, threshold;
    private IDistanceEncoder _encoder;
    
    private int consecutiveCorrectPositions = 0;
    
    boolean _stopWhenDone;
    
    double angleOffset;
    
    boolean _log;
    
    final private static String TAG = "RelativePIDAngleLogic";
    
    // Auto Reset variables
    //--------------------------------------
    
    boolean isAutoResetting = true;
    double autoResetDirection; // used for remembering which way to go during a reset
    double autoResetLocation;  ///angle where the reset switch is
    
    private DigitalInput stopSwitch;
    
    final private static double homingMotorPower = .25;
    
    // PID Calculation variables
    //--------------------------------------
    double kP;
    double kI;
    double kD;
    
    double errorSum = 0;
    double prevError = 0;
    
    final private static double errorLimit = 100000;
    
    
    /**
     * 
     * @param kP constant of pid
     * @param threshold acceptable error in degrees
     * @param stopWhenDone whether to stop controlling the motor when it's reached its target
     * @param encoder
     * @param stopSwitch a switch that, when <strong>low</strong>, signifies that the motor has reached its stop point.  If it 
     *  is provided, calling clearSpeedControlRun() will automatically, asynchronously run the motor until it hits this point.
     *  If it is null, calling clearSpeedControlRun() will just set the current position of the motor to the zero point.
     */
    public RelativePIDAngleLogic(double kP, double kI, double kD, double threshold, boolean stopWhenDone,
    		IDistanceEncoder encoder, DigitalInput stopSwitch, double stopSwitchLocation, boolean log)
    {
    	_refreshTime = 10;
        
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        
        _log = log;
        
        this.threshold = threshold;
        _encoder = encoder;
        
        _stopWhenDone = stopWhenDone;
        
        this.stopSwitch = stopSwitch; //TODO replace with limit switch class
        
        this.autoResetLocation = stopSwitchLocation;
        
        angleOffset = 0;
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
    	
    	double power = 0;
    	
    	//if we're auto resetting, do that instead of the normal algorithm
    	if(isAutoResetting)
    	{
    		if(!stopSwitch.get())
    		{
    			//Done resetting!  We are now at the auto reset location
    			isAutoResetting = false;
    			
    			power = 0;
    			
    			angleOffset = autoResetLocation; //we may not actually be at 0, so set the power accordingly
    		}
    		
    		power = homingMotorPower * autoResetDirection;
    	}
    	else
    	{
	    	double angle = getAngle();
	    	
	    	double error = RobotMath.angleDistance(angle, this.targetAngle, true);
	    	    	
	    	errorSum += error;
	    	
	    	if(errorSum > errorLimit)
	    	{
	    		Log.unusual(TAG, "I error sum of " + errorSum + " went over limit of " + errorLimit);
	    		//errorSum = errorLimit;
	    	}
	    	else if(errorSum < -errorLimit)
	    	{
	    		Log.unusual(TAG, "I error sum of " + errorSum + " went under limit of " + -errorLimit);
	    		//errorSum = -errorLimit;
	    	}
	    	
	        double output = error * kP + errorSum * kI + kD * (error - prevError);
	        
	        prevError = error;
	        
	       	if(_log)
	    	{
	            //Log.debug(TAG, "target: " + targetAngle + " current: " + angle + " error: " + error + " output: " + output);
	    	}
	
	        if(Math.abs(error) < threshold)
	        {
	        	++consecutiveCorrectPositions;
	        	return 0;
	        }
	        consecutiveCorrectPositions = 0;
	        
	        power = RobotMath.makeValidPower(output);
    	}
    	
    	return power;
    }

    @Override
    public synchronized void reset()
    {
    	errorSum = 0;
    	consecutiveCorrectPositions = 0;
    	
    	targetAngle = 0;
    	
    	if(stopSwitch != null)
    	{
    		isAutoResetting = true;
    		
    		//which way do we go?
    		autoResetDirection = RobotMath.sgn(RobotMath.angleDistance(_encoder.getDistanceInDegrees(), this.targetAngle, true));
    		
    	}
    }

    /**
     * Returns true if the motor is at the correct angle
     */
    public boolean isComplete()
    {	
        return _stopWhenDone && consecutiveCorrectPositions >= 5;
    }
    
    /**
     * Gets the angle of the motor in degrees.
     * 
     * This controller maintains an internal angle offset, so getting this info from the encoder may not work.
     * @return
     */
    public double getAngle()
    {
    	 return _encoder.getDistanceInDegrees() + angleOffset;
    }
}

