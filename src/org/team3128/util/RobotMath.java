package org.team3128.util;


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
    * @return the normalized angle on [-179, 180]
    */
   public static double normalizeAngle(double angle) {
       double theta = ((angle % 360) + 360) % 360;
       if (theta > 180) return (theta - 360);
       return theta;
   }

   /**
    * Finds the shortest distance between two angles.
    *
    * @param angle1 angle
    * @param angle2 angle
    * @return shortest angular distance between
    */
   public static double angleDistance(double angle1, double angle2) {
       double dist = normalizeAngle(angle2) - normalizeAngle(angle1);
       if (Math.abs(dist) > 180) {
           double sgn = RobotMath.sgn(dist);
           return -sgn * (360 - Math.abs(dist));
       }
       return dist;
   }

   public static double sgn(double n)
   {
       return Math.abs(n) / n;
   
   }
   
   public static int sgn(int n)
   {
       return Math.abs(n) / n;
   }
  
   /**
    *
    * @param pow motor power
    * <p/>
    * @return whether or not the power is valid
    */
   public static boolean isValidPower(double pow) {
       return (pow >= -1 && pow <= 1);
   }

   /**
    * Makes the provided power into a valid power level.
    * <p/>
    * @param pow power level to convert
    * <p/>
    * @return a properly-limited power level
    */
   public static double makeValidPower(double pow) {
       return (pow < -1 ? -1 : (pow > 1 ? 1 : pow));
   }

   /**
    * Determines the appropriate direction for a motor to turn.
    * <p/>
    * @param currentAngle the current angle of the motor
    * @param targetAngle the target angle of the motor
    * <p/>
    * @return a MotorDir
    */
   public static MotorDir getMotorDirToTarget(double currentAngle, double targetAngle) {
       currentAngle = RobotMath.normalizeAngle(currentAngle);
       targetAngle = RobotMath.normalizeAngle(targetAngle);
       int retDir = 1 * (Math.abs(currentAngle - targetAngle) > 180 ? 1 : -1) * (currentAngle - targetAngle < 0 ? -1 : 1);

       //MotorDir is syntactic sugar; could just return retDir
       if (currentAngle - targetAngle == 0 || currentAngle - targetAngle == 180) return MotorDir.NONE;
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
   
   public static double getMotorExpectedRPM(double power)
   {
	   //5310 is the max RPM at full power
	   return 5310 * power;
   }
   
   public static double getEstMotorPowerForRPM(double rpm)
   {
	   
	   //5310 is the max RPM at full power
	   return rpm / 5310;
   }
   
   /**
    * 
    * @param toFloor
    * @return An integer whose value is the same as or less than one lower than the argument.
    * Throws if the argument is too large to be an int.
    */
   public static int floor_double_int(double toFloor)
   {
	   double floored = Math.floor(toFloor);
	   if(toFloor > Integer.MAX_VALUE)
	   {
		   throw new IllegalArgumentException("The provided double is too large to be an int");
	   }
	   
	   return (int)floored;
   }

   private RobotMath() {}
}
