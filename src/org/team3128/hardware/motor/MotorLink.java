package org.team3128.hardware.motor;

import java.util.ArrayList;

import org.team3128.Log;

import edu.wpi.first.wpilibj.SpeedController;

/* 	Class Diagram:
 * The MotorLink, SpeedControl, and Limiter classes are related in somewhat convoluted ways.  
 * Hopefully this will clear it up.
 *   ___________                  ______________                  _________
 *  |           |                |              |                |         |
 *  | MotorLink | may have a --> | SpeedControl | may have a --> | Limiter |
 *  |___________|                |______________|                |_________|
 */

/**
 * MotorLink is the class used in this code to represent a motor or several linked ones.
 * It can operate with several
 * different varieties of motor control to use logic to control its speed, or none at all.
 */
public class MotorLink 
{
    private final ArrayList<SpeedController> motors = new ArrayList<SpeedController>();
    private SpeedControl spdControl;
    private boolean motorReversed = false;
    private double speedScalar = 1;

    public MotorLink()
    {
    	
    }
    
    public MotorLink(double powscl)
    {
    	this.speedScalar = powscl;
    }
    
    public MotorLink(SpeedControl spd)
    {
    	this.spdControl = spd;
    }
    
    /**
     * Construct MotorLink with a motor control and speed scalar.
     * @param spd
     * @param powscl
     */
    public MotorLink(SpeedControl spd, double powscl)
    {
    	this.speedScalar = powscl;
    	this.spdControl = spd;
    }
    
    /**
     * Reset the speed control.  This may or may not do anything, depending on which speed control is used.
     */
    public void clearSpeedControlRun()
    {
    	if(spdControl != null)
    	{
    		spdControl.clearControlRun();
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
     * Add a motor to the list of motors that is controlled by this MotorLink.
     * @param controller
     */
    public void addControlledMotor(SpeedController controller)
    {
    	motors.add(controller);
    }
    
    /**
     * Set the speed controller object that this motor should use. <br>
     * If null is passed, no speed control will be set.
     * @param spdControl
     */
    public void setSpeedController(SpeedControl spdControl)
    {
        if(this.spdControl != null && this.spdControl.isRunning()) 
        {
            this.spdControl.shutDown();
            throw new RuntimeException("MotorLink: The speed controller was changed when one was running.");
        }
        
        this.spdControl = spdControl;
    }

    /**
     * Sets the SpeedControl target, or the speed directly if there is no controller.
     * @param target
     */
    public void setControlTarget(double target)
    {
        if(spdControl == null)
        {
        	 setInternalSpeed(target);
        	 return;
        }
        if(!spdControl.isRunning())
        {
            Log.recoverable("MotorLink", "The speed controller's target was set, but it is not enabled.");
            return;
        }

        this.spdControl.setControlTarget(target);
    }

    /**
     * 
     * @return true if the speed control is running.
     */
    public boolean isSpeedControlRunning()
    {
    	if(spdControl == null)
    	{
    		return false;
    	}
    	return spdControl.isRunning();
    }

    /**
     * Start the speed control and set the control target.  If the control is already running,
     * all this does is clear and set the target of the control.
     * @param target
     */
    public void startControl(double target)
    {
    	if(spdControl == null)
    	{
    		Log.unusual("MotorLink", "startControl() was called on a motor link with no speed controller.");
    		return;
    	}
        this.spdControl.clearControlRun();
        this.spdControl.setControlTarget(target);
        this.spdControl.setControlledMotor(this);
        if(!spdControl.isRunning())
        {
        	spdControl.start();
        }
    }

    /**
     * Stop the speed control, if it exists
     */
    public void stopSpeedControl()
    {
    	if(spdControl != null)
    	{
    		spdControl.shutDown();
    	}
    }
}

