package org.team3128.autonomous;

import org.team3128.hardware.encoder.distance.IDistanceEncoder;
import org.team3128.hardware.motor.MotorLink;

/**
 * This class holds motor and encoder variables used in the auto programs.
 * They are global because it is too much of a pain to pass the same four things to
 * every autonomous command.
 * @author Jamie
 *
 */
public class AutoHardware
{
	public static IDistanceEncoder _encLeft;
	public static IDistanceEncoder _encRight;
	
	public static MotorLink _leftMotors;
	public static MotorLink _rightMotors;
}
