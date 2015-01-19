package org.team3128.hardware.misc;

import org.team3128.Options;
import org.team3128.hardware.motor.MotorLink;

import edu.wpi.first.wpilibj.DigitalInput;

public class CockArm
{
    volatile boolean _cockArmActive = true;

    DigitalInput _shooterTSensor;

    MotorLink _mShooter;

    Thread _thread;
    
    public CockArm(DigitalInput shooterTSensor, MotorLink mShooter)
    {
    	_shooterTSensor = shooterTSensor;
    	_mShooter = mShooter;
    }

    void execute()
    {
    	while(true)
    	{

        	if(!_cockArmActive || !Options.instance()._armEnabled)
        	{
        		return;
        	}

        	if(!_shooterTSensor.get()) //touch sensor pressed
        	{
        		_mShooter.setControlTarget(-1.0);
        	}
        	else
        	{
        		_mShooter.setControlTarget(0);
        	}

    	}
    	
    	
    }

    public void start()
    {
    	if(_thread == null || !_thread.isAlive())
    	{
        	_thread = new Thread(this::execute, "CockArm Thread");
        	_thread.start();
    	}
    }

    public void cancel()
    {
    	if(_thread != null && _thread.isAlive())
    	{
    		_thread.interrupt();
    	}
    }

    public void stopArmCock()
    {
    	_cockArmActive = false;
    }

    public void startArmCock()
    {
    	_cockArmActive = true;

    }
}
