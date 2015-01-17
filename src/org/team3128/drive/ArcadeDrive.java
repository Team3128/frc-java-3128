package org.team3128.drive;

import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.Listenable;
import org.team3128.listener.ListenerManager;

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
    public void steer()
    {
    	//read joystick values
    	double x1 = _listenerManager.getRawDouble(Listenable.JOY1X);
    	x1 = Math.abs(x1) > thresh ? Math.tanh(x1) : 0.0;
    	
    	
    	double y1 = _listenerManager.getRawDouble(Listenable.JOY1Y);
    	y1 = Math.abs(y1) > thresh ? Math.tanh(y1) : 0.0;

    	_spdR = (y1 + x1) / 2;
    	_spdL = (y1 - x1) / 2;

    	_leftMotors.setControlTarget(_spdL);
    	_rightMotors.setControlTarget(_spdR);
    }
}
