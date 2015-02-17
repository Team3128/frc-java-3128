package org.team3128.drive;

import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.ListenerManager;
import org.team3128.util.RobotMath;

public class ArcadeDrive
{
    double _spdL, _spdR;

    MotorLink _leftMotors;
    
    MotorLink _rightMotors;

    ListenerManager _listenerManager;
    
    public ArcadeDrive(MotorLink leftMotors, MotorLink rightMotors, ListenerManager listenerManager)
    {
    	_leftMotors = leftMotors;
    	_rightMotors = rightMotors;
    	_listenerManager = listenerManager;
    }
    
	//threshold below which joystick movements are ignored.
	final static double thresh = 0.2;
	/**
	 * 
	 * @param joyX horizontal control input
	 * @param joyY vertical control input
	 * @param throttle throttle control input scaled between 1 and -1 (-.8 is 10 %, 0 is 50%, 1.0 is 100%)
	 */
    public void steer(double joyX, double joyY, double throttle)
    {
    	//read joystick values
    	//x1 = Math.abs(x1) > thresh ? Math.tanh(x1) : 0.0;
    	joyX = Math.abs(joyX) > thresh ? -1 * joyX : 0.0;
    	
    	
    	//y1 = Math.abs(y1) > thresh ? Math.tanh(y1) : 0.0;
    	joyY = Math.abs(joyY) > thresh ? -1 * joyY : 0.0;
    	
    	//scale from 1 to -1 to 1 to 0
    	throttle =  ( throttle + 1) / 2;

    	if(throttle < .4)
    	{
    		throttle = .4;
    	}
    	else if(throttle > .8)
    	{
    		throttle = 1;
    	}
    	
    	_spdR = RobotMath.makeValidPower((joyY + joyX) * throttle);
    	_spdL = RobotMath.makeValidPower((joyY - joyX) * throttle);
    	
    	//Log.debug("ArcadeDrive", "x1: " + joyX + " throttle: " + throttle + " spdR: " + _spdR + " spdL: " + _spdL);

    	_leftMotors.setControlTarget(_spdL);
    	_rightMotors.setControlTarget(_spdR);
    }
}
