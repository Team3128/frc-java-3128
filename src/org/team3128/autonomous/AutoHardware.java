package org.team3128.autonomous;

import org.team3128.hardware.encoder.velocity.QuadratureEncoderLink;
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
	public static QuadratureEncoderLink _encLeft;
	public static QuadratureEncoderLink _encRight;
	
	public static MotorLink _leftMotors;
	public static MotorLink _rightMotors;
	
	public static Ultrasonic ultrasonic;
}
