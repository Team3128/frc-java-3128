package org.team3128.drive;

import java.util.ArrayList;

import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.hardware.motor.logic.RelativePIDAngleLogic;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;

public class SwerveDrive2
{
	//NOTE: ALL angles in degrees!
	
	private static class SwerveModule 
	{
		MotorGroup driveMotor;
		
		MotorGroup turnMotor;
		
		RelativePIDAngleLogic controller;
		
		double angleOnWheelbase; //position of the wheel in the robot's turning circle
		//90 degrees is exactly in the front
						
		double prevMotorPowerSign = 1;
		
		/**
		 * Sets the angle and power of the swerve module.  May also reverse the wheel to minimise movement time.
		 * @param targetAngle
		 * @param direction
		 */
		void setAngleAndPower(double targetAngle, double power)
		{			
			double currentAngle = controller.getAngle();
			
			double difference = RobotMath.sgn(targetAngle - currentAngle);
			
			if(difference > 90 && difference < 270)
			{
				targetAngle = targetAngle - 180;
				
				power *= -1;
			}
			
			//maintain previous state of reversal
			power *= prevMotorPowerSign;
			
			turnMotor.setControlTarget(targetAngle);
			driveMotor.setControlTarget(power);
		}
	}
	
	Gyro gyro;
		
	ArrayList<SwerveModule> modules = new ArrayList<SwerveModule>();
	
	int numModules = 0;
	
	boolean enableDriverOrientedControl = false;
	
	/**
	 * Construct a swerve drive with no gyro.  Driver-oriented control will be unavailable.
	 */
	public SwerveDrive2()
	{
		
	}
	
	/**
	 * Construct a swerve drive with a gyro.
	 * 
	 * You will still have to enable driver-oriented control by calling the function, though.
	 * @param gyro
	 */
	public SwerveDrive2(Gyro gyro)
	{
		this.gyro = gyro;
	}
	
	/**
	 * Set whether driver-oriented control is enabled.  If there is no gyro, it will always be disabled.
	 * @param enable
	 */
	public void setDriverOrientedControlEnabled(boolean enable)
	{
		enableDriverOrientedControl = (gyro != null && enable);
	}
	
	final static private double IS_TURNING_THRESHOLD = .2; //controller input threshold above which the robot is considered to be turning.

	public void drive(double controllerPowX, double controllerPowY, double controllerRotate)
	{
		// Convert input coordinates to polar
		//----------------------------------------------
		double headingAngle = Math.toDegrees(Math.atan2(controllerPowX, controllerPowY));
		
		//Do pythagorean theorem to figure out the magnitude
		double headingMagnitude = Math.sqrt(RobotMath.square(controllerPowX) + RobotMath.square(controllerPowY));
		
		if(enableDriverOrientedControl)
		{
			headingAngle += gyro.getAngle();
		}
		
		//Process turning
		// ---------------------------------------------
		boolean isTurning = Math.abs(controllerRotate) > IS_TURNING_THRESHOLD;
		
		//----------------------------------------------

		for(SwerveModule module : modules)
		{
			if(isTurning)
			{
				double turningOffset = RobotMath.sgn(controllerRotate) * -1 * 90.0;
				module.setAngleAndPower(module.angleOnWheelbase + turningOffset, Math.abs(controllerRotate));
			}
			else
			{
				module.setAngleAndPower(headingAngle, RobotMath.makeValidPower(headingMagnitude));
			}
		}
	}
	
	public void reset()
	{
		for(SwerveModule module : modules)
		{
			module.turnMotor.resetSpeedControl();
			module.driveMotor.setControlTarget(0);
		}
	}
	
	/**
	 * Add a module to the swerve drive.
	 * 
	 * Holy smokes that's a lot of parameters!
	 * 
	 * @param turnMotor the MotorGroup of the motor controlling the rotation of the wheel.  Rotating the motor forwards should rotate the wheel counterclockwise.
	 * @param driveMotor the MotorGroup of the motor moving the wheel.
	 * @param encoder the distance encoder (e.g. a quadrature encoder) hooked up to the turning motor.  COunterclockwise should be the positive direction
	 * @param homingSwitch
	 * @param homingSwitchOffset
	 * @param angleOnWheelbase
	 * @param homingOffset
	 * @param p
	 * @param i
	 * @param d
	 */
	public void addModule(MotorGroup turnMotor,
			MotorGroup driveMotor,
			IDistanceEncoder encoder,
			DigitalInput homingSwitch,
			double homingSwitchOffset,
			double angleOnWheelbase,
			double homingOffset,
			double p,
			double i,
			double d)
	{
		SwerveModule newModule = new SwerveModule();
		
		newModule.driveMotor = driveMotor;
		
		newModule.controller = new RelativePIDAngleLogic(p, i, d, 1, false, encoder, homingSwitch, homingSwitchOffset, true);
		
		turnMotor.setSpeedController(newModule.controller);
		turnMotor.startControl(0);
		newModule.turnMotor = turnMotor;
		
		newModule.angleOnWheelbase = angleOnWheelbase;
	}
}
