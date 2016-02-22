package org.team3128.main;

import org.team3128.drive.TankDrive;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.mechanisms.BackRaiserArm;
import org.team3128.hardware.mechanisms.TwoSpeedGearshift;
import org.team3128.hardware.misc.Piston;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.listener.ListenerManager;
import org.team3128.util.units.Length;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

/**
 * Main class for our 2016 practice robot, the Unladen Swallow.
 */
public  class MainUnladenSwallowPractice extends MainUnladenSwallow
{
	public MainUnladenSwallowPractice()
	{
		joystick = new Joystick(0);
		listenerManagerExtreme = new ListenerManager(joystick/*, new Joystick(1)*/);	
		powerDistPanel = new PowerDistributionPanel();
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(2, 3, 128, true);
		
		leftMotors = new MotorGroup();
		leftMotors.addMotor(new Talon(8));
		leftMotors.addMotor(new Talon(9));
		leftMotors.invert();		
		
		rightMotors = new MotorGroup();
		rightMotors.addMotor(new Talon(0));
		rightMotors.addMotor(new Talon(1));
		
		intakeSpinner = new MotorGroup();
		intakeSpinner.addMotor(new Victor(2));
		
		innerRoller = new MotorGroup();
		innerRoller.addMotor(new Victor(3));
		innerRoller.invert();
	
		drive = new TankDrive(leftMotors, rightMotors, leftDriveEncoder, rightDriveEncoder, 7.65 * Length.in * Math.PI, DRIVE_WHEELS_GEAR_RATIO, 26.125 * Length.in);
		//
		leftGearshiftPiston = new Piston(new Solenoid(2), new Solenoid(5),true,false);
		rightGearshiftPiston = new Piston(new Solenoid(0), new Solenoid(7),true,false);
		
		gearshift = new TwoSpeedGearshift(true, leftGearshiftPiston, rightGearshiftPiston);

		leftIntakePiston = new Piston(new Solenoid(4), new Solenoid(3),true,false);
		rightIntakePiston = new Piston(new Solenoid(1), new Solenoid(6),true,false);
		externalCompressor = new Compressor();
		externalCompressor.stop();
		
		
		backArmMotor = new CANTalon(0);
		
		backArmMotor.setEncPosition(0);
		backArmMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		backArmMotor.setForwardSoftLimit(0);
		backArmMotor.enableForwardSoftLimit(true);

		backArm = new BackRaiserArm(backArmMotor);
		
		lights = new PWMLights(17, 18, 19);
	}
	
	

}
