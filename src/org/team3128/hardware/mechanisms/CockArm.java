package org.team3128.hardware.mechanisms;

import org.team3128.RobotProperties;
import org.team3128.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Mechanism class to control the shooter of our 2014 robot, Sebastian.
 * 
 * @author Yousuf
 * @author Noah
 *
 */
public class CockArm
{
    volatile boolean _cockArmActive = true;

    DigitalInput _shooterTSensor;

    MotorGroup _mShooter;

    Thread _thread;
    
    public CockArm(DigitalInput shooterTSensor, MotorGroup mShooter)
    {
    	_shooterTSensor = shooterTSensor;
    	_mShooter = mShooter;
    }

    void execute()
    {
    	while(true)
    	{

        	if(!_cockArmActive || !RobotProperties.armEnabled)
        	{
        		return;
        	}

        	if(!_shooterTSensor.get()) //touch sensor pressed
        	{
        		_mShooter.setTarget(-1.0);
        	}
        	else
        	{
        		_mShooter.setTarget(0);
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
