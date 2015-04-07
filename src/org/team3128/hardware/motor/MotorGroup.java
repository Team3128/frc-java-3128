package org.team3128.hardware.motor;

import java.util.ArrayList;

import org.team3128.Log;

import edu.wpi.first.wpilibj.SpeedController;

/* 	Class Diagram:
 * The MotorGroup, MotorLogic, and Limiter classes are related in somewhat convoluted ways.  
 * Hopefully this will clear it up.
 *   ___________                  ____________                  _________
 *  |           |                |            |                |         |
 *  | MotorGroup | may have a --> | MotorLogic | may have a --> | Limiter |
 *  |___________|                |____________|                |_________|
 */

/**
 * MotorGroup is the class used in this code to represent a motor or several linked ones.
 * It can operate with several
 * different varieties of motor control to use logic to control its speed, or none at all.
 */
public class MotorGroup 
{
    private final ArrayList<SpeedController> motors = new ArrayList<SpeedController>();
    private MotorLogic motorLogic;
    private boolean motorReversed = false;
    private double speedScalar = 1;

    public MotorGroup()
    {
    	
    }
    
    public MotorGroup(double powscl)
    {
    	this.speedScalar = powscl;
    }
    
    public MotorGroup(MotorLogic spd)
    {
    	this.motorLogic = spd;
    }
    
    /**
     * Construct MotorGroup with a motor control and speed scalar.
     * @param spd
     * @param powscl
     */
    public MotorGroup(MotorLogic spd, double powscl)
    {
    	this.speedScalar = powscl;
    	this.motorLogic = spd;
    }
    
    /**
     * Reset the speed control.  This may or may not do anything, depending on which speed control is used.
     */
    public void clearSpeedControlRun()
    {
    	if(motorLogic != null)
    	{
    		motorLogic.clearControlRun();
    	}
    }

    /**
     * Make the motor go the other direction.
     */
    public void reverseMotor()
    {
    	motorReversed = !motorReversed;
    }
    
    /**
     * Set a speed scalar from 0 to 1 which will be applied to all motor powers set, after the speed control.
     * @param powScl
     */
    public void setSpeedScalar(double powScl)
    {
    	this.speedScalar = powScl;
    }
    
    /**
     * Get the speed scalar.
     * @param powScl
     * @return
     */
    public double getSpeedScalar()
    {
    	return this.speedScalar;
    }
    
    protected void setInternalSpeed(double pow)
    {
    	double powToSet = pow * speedScalar * (motorReversed ? -1.0 : 1.0);
    	
    	for(SpeedController motor : motors)
    	{
    		motor.set(powToSet);
    	}
    	
    }
    
    /**
     * Add a motor to the list of motors that is controlled by this MotorGroup.
     * @param controller
     */
    public void addControlledMotor(SpeedController controller)
    {
    	motors.add(controller);
    }
    
    /**
     * Set the speed controller object that this motor should use. <br>
     * If null is passed, no speed control will be set.
     * @param motorLogic
     */
    public void setSpeedController(MotorLogic spdControl)
    {
        if(this.motorLogic != null && this.motorLogic.isRunning()) 
        {
            this.motorLogic.shutDown();
            throw new RuntimeException("MotorGroup: The speed controller was changed when one was running.");
        }
        
        this.motorLogic = spdControl;
    }

    /**
     * Sets the MotorLogic target, or the speed directly if there is no controller.
     * @param target
     */
    public void setControlTarget(double target)
    {
        if(motorLogic == null)
        {
        	 setInternalSpeed(target);
        	 return;
        }
        if(!motorLogic.isRunning())
        {
            Log.recoverable("MotorGroup", "The speed controller's target was set, but it is not enabled.");
            return;
        }

        this.motorLogic.setControlTarget(target);
    }

    /**
     * 
     * @return true if the speed control is running.
     */
    public boolean isSpeedControlRunning()
    {
    	if(motorLogic == null)
    	{
    		return false;
    	}
    	return motorLogic.isRunning();
    }

    /**
     * Start the speed control and set the control target.  If the control is already running,
     * all this does is clear and set the target of the control.
     * @param target
     */
    public void startControl(double target)
    {
    	if(motorLogic == null)
    	{
    		Log.unusual("MotorGroup", "startControl() was called on a motor link with no speed controller.");
    		return;
    	}
        this.motorLogic.clearControlRun();
        this.motorLogic.setControlTarget(target);
        this.motorLogic.setControlledMotor(this);
        if(!motorLogic.isRunning())
        {
        	motorLogic.start();
        }
    }

    /**
     * Stop the speed control, if it exists
     */
    public void stopSpeedControl()
    {
    	if(motorLogic != null)
    	{
    		motorLogic.shutDown();
    	}
    }
}

