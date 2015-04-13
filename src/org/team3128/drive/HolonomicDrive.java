package org.team3128.drive;

import org.team3128.Options;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;
import org.team3128.listener.controller.ControllerXbox;

/**
 * Drive class for holonomic wheels
 * @author Jacob
 *
 */
public class HolonomicDrive
{
    double _spdLF, _spdLB, _spdRF, _spdRB;

    MotorGroup _driveLeftFront;
    MotorGroup _driveLeftBack;

    MotorGroup _driveRightFront;
    MotorGroup _driveRightBack;

    ListenerManager _listenerManager;
    
    public HolonomicDrive(MotorGroup driveLeftFront, MotorGroup driveLeftBack, MotorGroup driveRightFront, MotorGroup driveRightBack, ListenerManager listenerManager)
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
    	double joyX = _listenerManager.getRawAxis(ControllerXbox.JOY1X);
    	double joyY = _listenerManager.getRawAxis(ControllerXbox.JOY1Y);
    	
    	double mag = (10.0/7.0)*(Math.sqrt(joyX * joyX + joyY * joyY) - 0.3);
    	
    	joyX = mag * (2-mag) *Options.glidingSpeedConstant * (Math.abs(joyX) > thresh ? joyX : 0.0);
    	joyY = mag * (2-mag) * Options.glidingSpeedConstant * (Math.abs(joyY) > thresh ? joyY : 0.0);
    	
    	double joyTurn = _listenerManager.getRawAxis(ControllerXbox.JOY2X);
    	joyTurn =-1* Options.turningSpeedConstant * (Math.abs(joyTurn) > thresh ? joyTurn : 0.0);
    	
    	/*if (joyTurn > thresh) {
    		_spdLB = _spdRF = _spdLF = _spdRB = 0.4 *joyTurn;
    	}*/
    	
    	_spdLB = (joyY - joyX) + 1.2* joyTurn;
    	_spdRF = (joyY - joyX) - joyTurn;
    	
    	_spdLF = (joyY + joyX) + 1.2*joyTurn;
    	_spdRB = (joyY + joyX) - joyTurn;

    	_driveLeftFront.setControlTarget(_spdLF);
    	_driveLeftBack.setControlTarget(_spdLB);
    	_driveRightFront.setControlTarget(_spdRF);
    	_driveRightBack.setControlTarget(_spdRB);
    }
}
