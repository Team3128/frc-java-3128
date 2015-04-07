package org.team3128.autonomous;

import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
import org.team3128.hardware.mechanisms.ClawArm;
import org.team3128.hardware.motor.MotorLink;

import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * This class holds motor and encoder variables used in the auto programs.
 * They are global because it is too much of a pain to pass the same four things to
 * every autonomous command.
 * @author Jamie
 *
 */
public class AutoHardware
{
	public static QuadratureEncoderLink encLeft;
	public static QuadratureEncoderLink encRight;
	
	public static MotorLink leftMotors;
	public static MotorLink rightMotors;
	
	public static Ultrasonic ultrasonic;
	
	public static ClawArm clawArm;
}
