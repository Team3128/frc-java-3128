package org.team3128.util;

import org.team3128.Log;


/**
*
* @author Noah Sutton-Smolin
*/
public class RobotMath {
   /**
    * Limits the angle to between 0 and 359 degrees for all math. All angles
    * should be normalized before use.
    * <p/>
    * @param angle the angle to be normalized
    * <p/>
    * @return the normalized angle on [0, 359]
    */
   public static double normalizeAngle(double angle)
   {
       double theta = ((angle % 360) + 360) % 360;
       return theta;
   }

   /**
    * Finds the shortest distance between two angles.
    *
    * @param angle1 angle
    * @param angle2 angle
    * @param shortWay if true, go the shorter way to make moves always <= 180
    * @return shortest angular distance between
    */
   public static double angleDistance(double angle1, double angle2, boolean shortWay)
   {
       double dist = normalizeAngle(angle2) - normalizeAngle(angle1);
       
       if(shortWay && Math.abs(dist) > 180)
       {
           double sgn = RobotMath.sgn(dist);
           return -sgn * (360 - Math.abs(dist));
       }
       
       return dist;
   }

   
   /**
    * Standard-ish sign function
    * @param n
    * @return
    */
   public static double sgn(double n)
   {
	   if(n == 0)
	   {
		   return 0;
	   }
	   
       return Math.abs(n) / n;
   
   }
   
   public static int sgn(int n)
   {
	   if(n == 0)
	   {
		   return 0;
	   }
	   
       return Math.abs(n) / n;
   }
  
   /**
    * Check if a motor power is between -1 and 1 inclusive
    * @param pow motor power
    * <p/>
    * @return whether or not the power is valid
    */
   public static boolean isValidPower(double pow)
   {
       return (pow >= -1 && pow <= 1);
   }

   /**
    * Makes the provided power into a valid motor power level.
    * <p/>
    * @param pow power level to convert
    * <p/>
    * @return a properly-limited power level
    */
   public static double makeValidPower(double pow)
   {
       return (pow < -1 ? -1 : (pow > 1 ? 1 : pow));
   }

   /**
    * Determines the appropriate direction for a motor to turn to get to an angle.
    * <p/>
    * @param currentAngle the current angle of the motor
    * @param targetAngle the target angle of the motor
    * @param shortWay if true, go the shorter way to make moves always <= 180
    * <p/>
    * @return a MotorDir
    */
   public static MotorDir getMotorDirToTarget(double currentAngle, double targetAngle, boolean shortWay) 
   {
       currentAngle = RobotMath.normalizeAngle(currentAngle);
       targetAngle = RobotMath.normalizeAngle(targetAngle);
       int retDir = 1 * ((shortWay && Math.abs(currentAngle - targetAngle) > 180 )? 1 : -1) * (currentAngle - targetAngle < 0 ? -1 : 1);

       if (Math.abs(currentAngle - targetAngle) < .001) return MotorDir.NONE;
       return (retDir == 1 ? MotorDir.CW : MotorDir.CCW);
   }

   /**
    * Convert degrees to radians
    * @param angle degrees
    * @return radians
    */
   public static double dTR(double angle) {return Math.PI * angle / 180.0;}

   /**
    * Convert radians to degrees
    *
    * @param rad radians
    * @return degrees
    */
   public static double rTD(double rad) {return rad * (180.0 / Math.PI);}
   
   /**
    * Clamps value from (inclusive) minimum to maximum 
    * @param value
    * @param minimum
    * @param maximum
    * @return
    */
   public static int clampInt(int value, int minimum, int maximum)
   {
	   if(!(minimum <= maximum))
	   {
		   Log.debug("RobotMath", "...what?  clampInt() called with insane arguments");
	   }
	   return Math.min(Math.max(value, minimum), maximum); 
   }
   
   /**
    * Clamps value from (inclusive) minimum to maximum 
    * @param value
    * @param minimum
    * @param maximum
    * @return
    */
   public static double clampDouble(double value, double minimum, double maximum)
   {
	   return Math.min(Math.max(value, minimum), maximum); 
   }
   
   public static final double SQUARE_ROOT_TWO = Math.sqrt(2.0);
   
   public static double getCIMExpectedRPM(double power)
   {
	   //5310 is the max RPM of a CIM at full power
	   return 5310 * power;
   }
   
   public static double getEstCIMPowerForRPM(double rpm)
   {
	   
	   //5310 is the max RPM of a CIM at full power
	   return rpm / 5310;
   }
   
   /**
    * 
    * @param toFloor
    * @return An integer whose value is the same as or less than one lower than the argument.
    * Throws if the argument is too large to be an int, is NaN, or posiive or negative infinity.
    */
   public static int floor_double_int(double toFloor)
   {
	   double floored = Math.floor(toFloor);
	   
	   if(!Double.isFinite(floored) || toFloor > Integer.MAX_VALUE)
	   {
		   throw new IllegalArgumentException("The provided double cannot be represented by an int");
	   }
	   
	   return (int)floored;
   }
   
   /**
    * 
    * @param toCeil
    * @return An integer whose value is the same or less than one higher than the argument.
    * Throws if the argument is too large to be an int, is NaN, or posiive or negative infinity.
    */
   public static int ceil_double_int(double toCeil)
   {
	   double ceilinged = Math.ceil(toCeil);
	   if(!Double.isFinite(ceilinged) || ceilinged > Integer.MAX_VALUE)
	   {
		   throw new IllegalArgumentException("The provided double cannot be represented by an int");
	   }
	   
	   return (int)ceilinged;
   }

   //hidden constructor
   private RobotMath() {}

	/**
	 * Convert cm of robot movement to degrees of wheel movement
	 * @param cm
	 * @param wheelCircumference the circumference of the wheels
	 * @return
	 */
	public static double cmToDegrees(double cm, double wheelCircumference)
	{
		return (360 / wheelCircumference) * cm;
	}
	
	
	/**
	 * Squares the argument.  Easier than Math.pow(number, 2).
	 * @param number
	 * @return
	 */
	public static double square(double number)
	{
		return number * number;
	}
	
	/**
	 * Squares the argument.  Easier than RobotMath.floor_double_int(Math.pow(number, 2)).
	 * @param number
	 * @return
	 */
	public static int square(int number)
	{
		return number * number;
	}
}
