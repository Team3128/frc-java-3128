package org.team3128.drive;

import org.team3128.Log;
import org.team3128.RobotProperties;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;

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
    
    public HolonomicDrive(MotorGroup driveLeftFront, MotorGroup driveLeftBack, MotorGroup driveRightFront, MotorGroup driveRightBack, ListenerManager listenerManager)
    {
    	_driveLeftFront = driveLeftFront;
    	_driveLeftBack = driveLeftBack;
    	_driveRightFront = driveRightFront;
    	_driveRightBack = driveRightBack;
    }
    
	//threshold below which joystick movements are ignored.
	final static double thresh = 0.3;
    public void steer(double joyX, double joyY, double joyTurn, double throttle)
    {
    	double mag = (10.0/7.0)*(Math.sqrt(joyX * joyX + joyY * joyY) - 0.3);
    	
    	joyX = mag * (2-mag) *RobotProperties.glidingSpeedConstant * (Math.abs(joyX) > thresh ? joyX : 0.0);
    	joyY = mag * (2-mag) * RobotProperties.glidingSpeedConstant * (Math.abs(joyY) > thresh ? joyY : 0.0);
    	
    	joyTurn =-1* RobotProperties.turningSpeedConstant * (Math.abs(joyTurn) > thresh ? joyTurn : 0.0);
    	
    	if (joyTurn > thresh) {
    		_spdLB = _spdRF = _spdLF = _spdRB = 0.4 *joyTurn;
    	}
    	
    	_spdLB = (joyY - joyX) + 1.2* joyTurn;
    	_spdRF = (joyY - joyX) - joyTurn;
    	
    	_spdLF = (joyY + joyX) + 1.2*joyTurn;
    	_spdRB = (joyY + joyX) - joyTurn;
    	
    	Log.debug("HolonomicDrive", String.format("joyY: %f, joyTurn: %f, spdLB: %f, spdRF: %f, spdLF %f, spdRB: %f", joyY, joyTurn, _spdLB, _spdRF, _spdLF, _spdRB));

    	_driveLeftFront.setControlTarget(_spdLF);
    	_driveLeftBack.setControlTarget(_spdLB);
    	_driveRightFront.setControlTarget(_spdRF);
    	_driveRightBack.setControlTarget(_spdRB);
    }
}

