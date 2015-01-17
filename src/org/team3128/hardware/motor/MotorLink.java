package org.team3128.hardware.motor;

import java.util.ArrayList;

import org.team3128.Log;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * MotorLink is the class used in this code to represent a motor or several linked ones.
 * It can operate with several
 * different varieties of motor control or none at all.
 */
public class MotorLink {
    private final ArrayList<SpeedController> motors = new ArrayList<SpeedController>();
    private MotorControl spdControl;
    private boolean motorReversed = false;
    private double speedScalar = 1;

    public MotorLink()
    {
    	
    }
    
    public MotorLink(double powscl)
    {
    	this.speedScalar = powscl;
    }
    
    public MotorLink(MotorControl spd)
    {
    	this.spdControl = spd;
    }
    
    public MotorLink(MotorControl spd, double powscl)
    {
    	this.speedScalar = powscl;
    	this.spdControl = spd;
    }

    public void reverseMotor()
    {
    	motorReversed = !motorReversed;
    }
    
    public void setSpeedScalar(double powScl)
    {
    	this.speedScalar = powScl;
    }
    
    public double getSpeedScalar(double powScl)
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
    
    public void addControlledMotor(SpeedController controller)
    {
    	motors.add(controller);
    }
    
    public void setSpeed(double pow)
    {
        if(this.spdControl != null && spdControl.isRunning())
        {
            this.spdControl.shutDown();
            Log.unusual("MotorLink", "The motor power was set from outside the speed controller, so the controller was canceled.");
        }
        setInternalSpeed(pow);
    }
    
    public void setSpeedController(MotorControl spdControl)
    {
        if(this.spdControl != null && this.spdControl.isRunning()) 
        {
            this.spdControl.shutDown();
            Log.fatal("MotorLink", "The speed controller was changed when one was running.");
        }
        
        this.spdControl = spdControl;
    }

    public void setControlTarget(double target) {
        if (this.spdControl == null) {
            Log.recoverable("MotorLink", "The speed controller's target was set, but none exists.");
            return;
        }
        if(!spdControl.isRunning())
        {
            Log.recoverable("MotorLink", "The speed controller's target was set, but it is not enabled.");
            return;
        }

        this.spdControl.setControlTarget(target);
    }

    public boolean isSpeedControlRunning()
    {
    	return spdControl.isRunning();
    }
    
    public void setSpeedControlTarget(double target)
    {
    	this.spdControl.setControlTarget(target);
    }

    public void startControl(double target)
    {
        this.spdControl.clearControlRun();
        this.spdControl.setControlTarget(target);
        this.spdControl.setControlledMotor(this);
        spdControl.start();
    }

    public void stopSpeedControl()
    {
        spdControl.shutDown();
    }
}

