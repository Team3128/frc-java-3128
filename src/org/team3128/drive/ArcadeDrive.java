package org.team3128.drive;

import org.team3128.Log;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerSaitekX55;
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
	final static double thresh = 0.1;
    public void steer()
    {
    	//read joystick values
    	double x1 = _listenerManager.getRawAxis(ControllerSaitekX55.TWIST);
    	//x1 = Math.abs(x1) > thresh ? Math.tanh(x1) : 0.0;
    	x1 = Math.abs(x1) > thresh ? x1 : 0.0;
    	
    	
    	double y1 = _listenerManager.getRawAxis(ControllerSaitekX55.JOYY);
    	//y1 = Math.abs(y1) > thresh ? Math.tanh(y1) : 0.0;
    	y1 = Math.abs(y1) > thresh ? y1 : 0.0;

    	_spdR = RobotMath.makeValidPower(y1 + x1);
    	_spdL = RobotMath.makeValidPower(y1 - x1);
    	
    	Log.debug("ArcadeDrive", "spdR: " + _spdR + " spdL: " + _spdL);

    	_leftMotors.setControlTarget(_spdL);
    	_rightMotors.setControlTarget(_spdR);
    }
}
