package org.team3128.drive;

import java.util.ArrayList;

import org.team3128.hardware.motor.MotorGroup;
import org.team3128.hardware.motor.logic.AbsolutePIDAngleLogic;
import org.team3128.util.Pair;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SpeedController;

public class SwerveDrive2
{
	//NOTE: ALL angles in degrees!
	
	private static class SwerveModule 
	{
		MotorGroup driveMotor;
		
		MotorGroup turnMotor;
		
		AbsolutePIDAngleLogic controller;
		
		double angleOnRobot; //90 degrees is straight ahead
	}
	
	Gyro gyro;
		
	ArrayList<Pair<Double, SpeedController>> modules = new ArrayList<Pair<Double, SpeedController>>();
	
	int numModules = 0;
	
	boolean enableDriverOrientedControl = false;
	
	/**
	 * Construct a swerve drive with no gyro.  Driver-oriented control will be unavailable.
	 */
	public SwerveDrive2()
	{
		
	}
	
	/**
	 * Construct a swerve drive with a gyro
	 * @param gyro
	 */
	public SwerveDrive2(Gyro gyro)
	{
		setGyro(gyro);
	}
	
	/**
	 * Set the gyro in use
	 * @param gyro
	 */
	public void setGyro(Gyro gyro)
	{
		this.gyro = gyro;

		enableDriverOrientedControl = gyro != null;
		
	}

	public void drive(double controllerPowX, double controllerPowY, double controllerRotate)
	{
		double headingAngle = Math.toDegrees(Math.atan2(controllerPowX, controllerPowY));
	}
}
