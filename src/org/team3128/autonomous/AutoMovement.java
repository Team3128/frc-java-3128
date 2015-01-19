package org.team3128.autonomous;

import static java.lang.Math.abs;

import org.team3128.Options;
import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.util.RobotMath;

public class AutoMovement
{
	IDistanceEncoder _encLeft;
	IDistanceEncoder _encRight;
	
	MotorLink _leftMotors;
	MotorLink _rightMotors;
	
	private void clearEncoders()
	{
		_encLeft.clear();
		_encRight.clear();
	}
	
	private double cmToDegrees(double cm)
	{
		return Options.instance()._degreesPercm;
	}
	
	private void stopMovement()
	{
		_leftMotors.setControlTarget(0);
		_rightMotors.setControlTarget(0);
	}
	
	/**
	 * quick sleep function so you don't have to catch an InterruptedException
	 * @param msec
	 */
	private void sleep(int msec)
	{
		try
		{
			Thread.sleep(msec);
		} 
		catch (InterruptedException e)
		{
			return;
		}
	}
	
	private void killRobot(String cause)
	{
		throw new RuntimeException("Error in automatic movement - " + cause + "\nRobot shut down!");
	}
	
	/**
	 * move forward the given amount of centimeters
	 * @param cm
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
	void moveForwardDist(int cm, int msec)
	{
		clearEncoders();
		int enc = RobotMath.floor_double_int(abs(cmToDegrees(cm)));
		int norm = RobotMath.sgn(cm);
		long startTime = System.currentTimeMillis();

		
		_leftMotors.setControlTarget(.25*norm);
		_rightMotors.setControlTarget(.25*norm);
		while(_encLeft.getDistance() < enc && _encRight.getDistance() < enc)
		{
			if(msec != 0 && System.currentTimeMillis() - startTime > msec)
			{
				killRobot("Move Overtime");
			}
			
			sleep(10);
		}
		stopMovement();
	}
	
	/**
	 * arc to the right (set LEFT motors)
	 * @param degs
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
	void arcRight(float degs, int msec) 
	{
		double enc = cmToDegrees((2.0*Math.PI*Options.instance()._wheelBase)*(abs(degs)/360.0));
		clearEncoders();
		_leftMotors.setControlTarget(RobotMath.sgn(degs) * .4);
		long startTime = System.currentTimeMillis();
		while(_encLeft.getDistance() < enc)
		{
			if(msec != 0 && System.currentTimeMillis() - startTime > msec)
			{
				killRobot("Turn Overtime");
			}
			sleep(10);
		}
		stopMovement();
	}
	
	/**
	 * arc to the left (set RIGHT motors)
	 * @param degs
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
	void arcLeft(float degs, int msec) 
	{
		double enc = cmToDegrees((2.0*Math.PI*Options.instance()._wheelBase)*(abs(degs)/360.0));
		clearEncoders();
		_rightMotors.setControlTarget(RobotMath.sgn(degs) * .4);
		long startTime = System.currentTimeMillis();
		while(_encRight.getDistance() < enc)
		{
			if(msec != 0 && System.currentTimeMillis() - startTime > msec)
			{
				killRobot("Turn Overtime");
			}
			
			sleep(10);
		}
		stopMovement();
	}
	
	/**
	 * Turn right in place.
	 * @param degs
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
	void turnRight(float degs, int msec)
	{
		double enc = cmToDegrees((Math.PI*Options.instance()._wheelBase)*(abs(degs)/360.0));
		clearEncoders();
		_leftMotors.setControlTarget(-1*RobotMath.sgn(degs) * .3);
		_rightMotors.setControlTarget(RobotMath.sgn(degs)* .3);
		long startTime = System.currentTimeMillis();
		
		while(_encRight.getDistance() < enc)
		{
			if(msec != 0 && System.currentTimeMillis() - startTime > msec)
			{
				killRobot("Turn Overtime");
			}
			
			sleep(10);
		}
		stopMovement();
	}
	
	/**
	 * Turn left in place.
	 * @param degs
	 * @param msec How long the move should take. If set to 0, do not time the move
	 */
	void turnLeft(float degs, int msec)
	{
		double enc = cmToDegrees((Math.PI*Options.instance()._wheelBase)*(abs(degs)/360.0));
		clearEncoders();
		_leftMotors.setControlTarget(RobotMath.sgn(degs)* .3);
		_rightMotors.setControlTarget(-1 * RobotMath.sgn(degs)* .3);
		long startTime = System.currentTimeMillis();
		
		while(_encLeft.getDistance() < enc)
		{
			if(msec != 0 && System.currentTimeMillis() - startTime > msec)
			{
				killRobot("Turn Overtime");
			}
			
			sleep(10);
		}
		stopMovement();
	}
}
