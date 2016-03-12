package org.team3128.main;

import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.lights.PWMLights;
import org.team3128.hardware.mechanisms.BackRaiserArm;
import org.team3128.hardware.mechanisms.TwoSpeedGearshift;
import org.team3128.hardware.misc.Piston;
import org.team3128.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 * Main class for our 2016 robot, the Unladen Swallow.
 */
public  class MainUnladenSwallowCompetition extends MainUnladenSwallow
{
	public MainUnladenSwallowCompetition()
	{
		
		leftDriveEncoder = new QuadratureEncoderLink(0,	1, 128, false);
		rightDriveEncoder = new QuadratureEncoderLink(2, 3, 128, true);
		
		leftMotors = new MotorGroup();
		leftMotors.addMotor(new Talon(1));
		leftMotors.addMotor(new Talon(2));
		leftMotors.invert();		
		leftMotors.setSpeedScalar(1.07);
		
		rightMotors = new MotorGroup();
		rightMotors.addMotor(new Talon(3));
		rightMotors.addMotor(new Talon(4));
		rightMotors.setSpeedScalar(1);
		
		intakeSpinner = new MotorGroup();
		intakeSpinner.addMotor(new Talon(0));
		intakeSpinner.invert();
		
		innerRoller = new MotorGroup();
		innerRoller.addMotor(new Talon(5));
	
		//
		leftGearshiftPiston = new Piston(new Solenoid(3), new Solenoid(5),false,false);
		rightGearshiftPiston = new Piston(new Solenoid(2), new Solenoid(6),false,false);
		
		gearshift = new TwoSpeedGearshift(true, leftGearshiftPiston, rightGearshiftPiston);

		leftIntakePiston = new Piston(new Solenoid(1), new Solenoid(4),true,false);
		rightIntakePiston = new Piston(new Solenoid(0), new Solenoid(7),true,false);
		compressor = new Compressor();		
		
		backArmMotor = new CANTalon(0);
		
		backArmMotor.setEncPosition(0);
		backArmMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		backArmMotor.setForwardSoftLimit(0);
		backArmMotor.enableForwardSoftLimit(true);

		backArm = new BackRaiserArm(backArmMotor, 0.022428);
		
		lights = new PWMLights(17, 18, 19);
		
	}
	
	

}
