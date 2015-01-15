package org.team3128.drive;

import org.team3128.Options;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.Listenable;
import org.team3128.listener.ListenerManager;

public class HolonomicDrive
{
    double _spdLF, _spdLB, _spdRF, _spdRB;

    MotorLink _driveLeftFront;
    MotorLink _driveLeftBack;

    MotorLink _driveRightFront;
    MotorLink _driveRightBack;

    ListenerManager _listenerManager;
    
    public HolonomicDrive(MotorLink driveLeftFront, MotorLink driveLeftBack, MotorLink driveRightFront, MotorLink driveRightBack, ListenerManager listenerManager)
    {
    	_driveLeftFront = driveLeftFront;
    	_driveLeftBack = driveLeftBack;
    	_driveRightFront = driveRightFront;
    	_driveRightBack = driveRightBack;
    	_listenerManager = listenerManager;
    }
    
	//threshold below which joystick movements are ignored.
	final static double thresh = 0.3;
    public void steer()
    {
    	//read joystick values
    	double joyX = _listenerManager.getRawDouble(Listenable.JOY1X);
    	double joyY = _listenerManager.getRawDouble(Listenable.JOY1Y);
    	
    	double mag = (10.0/7.0)*(Math.sqrt(joyX * joyX + joyY * joyY) - 0.3);
    	
    	joyX = mag * (2-mag) *Options.instance()._glidingSpeedConstant * (Math.abs(joyX) > thresh ? joyX : 0.0);
    	joyY = mag * (2-mag) * Options.instance()._glidingSpeedConstant * (Math.abs(joyY) > thresh ? joyY : 0.0);
    	
    	double joyTurn = _listenerManager.getRawDouble(Listenable.JOY2X);
    	joyTurn =-1* Options.instance()._turningSpeedConstant * (Math.abs(joyTurn) > thresh ? joyTurn : 0.0);
    	
    	/*if (joyTurn > thresh) {
    		_spdLB = _spdRF = _spdLF = _spdRB = 0.4 *joyTurn;
    	}*/
    	
    	_spdLB = (joyY - joyX) + 1.2* joyTurn;
    	_spdRF = (joyY - joyX) - joyTurn;
    	
    	_spdLF = (joyY + joyX) + 1.2*joyTurn;
    	_spdRB = (joyY + joyX) - joyTurn;

    	_driveLeftFront.setSpeed(_spdLF);
    	_driveLeftBack.setSpeed(_spdLB);
    	_driveRightFront.setSpeed(_spdRF);
    	_driveRightBack.setSpeed(_spdRB);
    }
}
