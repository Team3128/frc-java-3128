package org.team3128.drive;

import org.team3128.RobotProperties;
import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorGroup;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.Gyro;


/**
 * Drive class for the swerve drive on our 2014 robot.
 * @author Jamie
 *
 */
public class SwerveDrive
{
    private double vel, theta, rot, xVel, yVel;
    private double spdL, spdR, spdB;
    private double angL, angR, angB;
    private double maxVel = 8, maxRot = RobotMath.dTR(345), c = 0.3;
    private final double xPosL = -12.77374, xPosR = 12.77374, xPosB = 0.0;
    private final double yPosL = 7.375, yPosR = 7.375, yPosB = -14.75;
    
    private Gyro _gyr;
    
    private MotorGroup _rotFL, _rotFR, _rotBk;
    
    private MotorGroup _drvFL, _drvFR, _drvBk;
    
    private IAngularEncoder _encFL, _encFR, _encBk;

    public double[] optimizeSwerve(double ang1, double ang2, double vel)
    {
    	
    	//hopefully all encoders are all using the same type of encoders
    	//otherwise the canRevolveMultipleTimes() will be wrong. -Jamie
        double a = RobotMath.angleDistance(ang2, ang1, _encFL.canRevolveMultipleTimes());
        double o[] = new double[2];
        if (Math.abs(a) > 90) {
            o[0] = ang2 + 180.0;
            o[1] = -vel;
        } else {
            o[0] = ang2;
            o[1] = vel;
        }
        return o;
    }

    public void execute(double joyX1, double joyY1, double joyX2) {
        double thresh = 0.2;

        double x1 = Math.abs(joyX1) > thresh ? joyX1 : 0.0;
        double y1 = Math.abs(joyY1) > thresh ? -joyY1 : 0.0;
        double x2 = Math.abs(joyX2) > thresh ? joyX2 : 0.0;

        vel = -(Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2)));
        rot = maxRot * x2 * 0.3;

        if (Math.abs(vel) > 0.1)
            theta = RobotMath.rTD(Math.atan2(y1, x1)) + _gyr.getAngle() + RobotProperties.gyrBias;
        else 
            vel = 0;

        xVel = maxVel * vel * Math.cos(RobotMath.dTR(theta));
        yVel = maxVel * vel * Math.sin(RobotMath.dTR(theta));

        spdL = Math.sqrt(Math.pow(xVel + (c*rot * yPosL), 2) + Math.pow(yVel - (c*rot * xPosL), 2));
        angL = RobotMath.rTD(Math.atan2(yVel + (c*rot * xPosL), xVel - (c*rot * yPosL)));

        spdB = Math.sqrt(Math.pow(xVel + (c*rot * yPosB), 2) + Math.pow(yVel - (c*rot * xPosB), 2));
        angB = 180.0 / Math.PI * (Math.atan2(yVel + (c*rot * xPosB), xVel - (c*rot * yPosB)));

        spdR = Math.sqrt(Math.pow(xVel + (c*rot * yPosR), 2) + Math.pow(yVel - (c*rot * xPosR), 2));
        angR = 180.0 / Math.PI * (Math.atan2(yVel + (c*rot * xPosR), xVel - (c*rot * yPosR)));

        double[] r = optimizeSwerve(_encFR.getAngle(), angR, spdR);
        double[] l = optimizeSwerve(_encFL.getAngle(), angL, spdL);
        double[] b = optimizeSwerve(_encBk.getAngle(), angB, spdB);
        
        _rotFR.setControlTarget(r[0]+(x1 == 0 && x2 != 0 ? 0.1 : 0));
        _rotFL.setControlTarget(l[0]+(x1 == 0 && x2 != 0 ? 0.1 : 0));
        _rotBk.setControlTarget(b[0]+(x1 == 0 && x2 != 0 ? 0.1 : 0));
        
        r[1] /= maxVel;
        l[1] /= maxVel;
        b[1] /= maxVel;
        
        if (Math.abs(r[1]) > 1 || Math.abs(l[1]) > 1 || Math.abs(b[1]) > 1) {
            double scl = Math.max(Math.abs(r[1]), Math.max(Math.abs(l[1]), Math.abs(b[1])));
            r[1] /= scl;
            l[1] /= scl;
            b[1] /= scl;
        }
        
        _drvFR.setControlTarget(-r[1]/1.0);
        _drvFL.setControlTarget(-l[1]/1.0);
        _drvBk.setControlTarget(-b[1]/1.0);
    }


	public SwerveDrive(Gyro gyr, MotorGroup rotFL, MotorGroup rotFR,
			MotorGroup rotBk, MotorGroup drvFL, MotorGroup drvFR,
			MotorGroup drvBk, IAngularEncoder encFL, IAngularEncoder encFR,
			IAngularEncoder encBk)
	{
		_gyr = gyr;
		_rotFL = rotFL;
		_rotFR = rotFR;
		_rotBk = rotBk;
		_drvFL = drvFL;
		_drvFR = drvFR;
		_drvBk = drvBk;
		_encFL = encFL;
		_encFR = encFR;
		_encBk = encBk;
	}
}
