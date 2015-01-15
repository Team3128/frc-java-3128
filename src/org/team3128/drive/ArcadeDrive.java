package org.team3128.drive;

import java.util.ArrayList;

import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.Listenable;
import org.team3128.listener.ListenerManager;

public class ArcadeDrive
{
    double _spdL, _spdR;

    ArrayList<MotorLink> _leftMotors;

    ArrayList<MotorLink> _rightMotors;

    ListenerManager _listenerManager;
    
    public ArcadeDrive(ListenerManager listenerManager)
    {
    	_leftMotors = new ArrayList<MotorLink>();
    	_rightMotors = new ArrayList<MotorLink>();
    	_listenerManager = listenerManager;
    }
    
    /**
     * Add a motor on the left side to be controlled
     * @param motor
     */
    public void addLeftMotor(MotorLink motor)
    {
    	_leftMotors.add(motor);
    }
    
    /**
     * Add a motor on the right side to be controlled
     * @param motor
     */
    public void addRightMotor(MotorLink motor)
    {
    	_rightMotors.add(motor);
    }
    
	//threshold below which joystick movements are ignored.
	final static double thresh = 0.2;
    public void steer()
    {
    	//read joystick values
    	double x1 = _listenerManager.getRawDouble(Listenable.JOY1X);
    	x1 = Math.abs(x1) > thresh ? x1 : 0.0;
    	
    	
    	double y1 = _listenerManager.getRawDouble(Listenable.JOY1Y);
    	y1 = Math.abs(y1) > thresh ? y1 : 0.0;

    	_spdR = (y1 + x1) / 2;
    	_spdL = (y1 - x1) / 2;

    	for(MotorLink motor : _leftMotors)
    	{
    		motor.setSpeed(_spdL);
    	}
    	
    	for(MotorLink motor : _rightMotors)
    	{
    		motor.setSpeed(_spdR);
    	}
    }
}
