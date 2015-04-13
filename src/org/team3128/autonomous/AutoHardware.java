package org.team3128.autonomous;

import org.team3128.Global;
import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * This class holds motor and encoder variables used in the auto programs.
 * They are global because it is too much of a pain to pass the same few things to
 * every autonomous command.
 * @author Jamie
 *
 */
public class AutoHardware
{
	public static QuadratureEncoderLink encLeft;
	public static QuadratureEncoderLink encRight;
	
	public static MotorGroup leftMotors;
	public static MotorGroup rightMotors;
	public static MotorLink _frontHookMotor;
	
	public static PowerDistributionPanel _distPanel;
	
	public static Ultrasonic ultrasonic;
	
	public static ClawArm clawArm;
	
	public static Global global;
}
