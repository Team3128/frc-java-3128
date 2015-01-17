package org.team3128.drive;

import org.team3128.hardware.encoder.angular.IAngularEncoder;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.listener.Listenable;
import org.team3128.listener.ListenerManager;
import org.team3128.util.Pair;
import org.team3128.util.RobotMath;

import edu.wpi.first.wpilibj.Gyro;

public class SwerveDrive
{
	 double dimConst = 10;
     double xPosL = -12.77374 / dimConst, xPosR = 12.77374 / dimConst, xPosB = 0.0 / dimConst;
     double yPosL = 7.375 / dimConst, yPosR = 7.375 / dimConst, yPosB = -14.75 / dimConst;
     double vel, theta, rot, xVel, yVel;
     double spdL, spdR, spdB;
     double angL, angR, angB;
     
    Gyro _gyr;

    MotorLink _rotFR;

    MotorLink _rotFL;

    MotorLink _rotBk;

    MotorLink _drvFR;

    MotorLink _drvFL;

    MotorLink _drvBk;
    
    IAngularEncoder frontREncoder;
    IAngularEncoder frontLEncoder;
    IAngularEncoder backEncoder;

    ListenerManager _listenerManager;

	Pair<Double, Double> optimizeSwerve(double ang1, double ang2, double vel)
	{
		double a = RobotMath.angleDistance(ang2, ang1);

		Pair<Double, Double> returnVals = new Pair<Double, Double>();
		if (Math.abs(a) > 90)
		{
			returnVals.left = ang2 + 180.0;
			returnVals.right = -vel;
		}
		else
		{
			returnVals.left = ang2;
			returnVals.right = vel;
		}
		return returnVals;
	}

	final static int epsilon = 4;
	private void initSwerve()
	{
		while(!(frontLEncoder.getAngle() < epsilon &&
				frontREncoder.getAngle() < epsilon &&
				backEncoder.getAngle() < epsilon))
		{
			_rotFL.setControlTarget(frontLEncoder.getAngle() < epsilon ? 0 : .5);
			_rotFR.setControlTarget(frontREncoder.getAngle() < epsilon ? 0 : .5);
			_rotBk.setControlTarget(backEncoder.getAngle() < epsilon ? 0 : .5);
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e)
			{
				return;
			}
		}
	}

	public SwerveDrive(Gyro gyr, MotorLink rotFR, IAngularEncoder encFR, MotorLink rotFL, IAngularEncoder encFL, MotorLink rotBk, IAngularEncoder encBk, MotorLink drvFR, MotorLink drvFL, MotorLink drvBk, ListenerManager listenerManager)
	{
	    _gyr = gyr;

	    _rotFR = rotFR;
	    frontREncoder = encFR;
	    
	    _rotFL = rotFL;
	    frontLEncoder = encFL;
	    
	    _rotBk = rotBk;
	    backEncoder = encBk;
	    
	    _drvFR = drvFR;

	    _drvFL = drvFL;

	    _drvBk = drvBk;

	    _listenerManager = listenerManager;
		new Thread(this::initSwerve, "Swerve Drive Init Thread").start();
	}

	void steer()
	{
	    double thresh = 0.2;

	    double x1 = _listenerManager.getRawDouble(Listenable.JOY1X);
	    x1 = Math.abs(x1) > thresh ? x1 : 0.0;
	    
	    double y1 = _listenerManager.getRawDouble(Listenable.JOY1Y);
	    y1 = Math.abs(y1) > thresh ? -y1 : 0.0;
	   
	    double x2 = _listenerManager.getRawDouble(Listenable.JOY2X);
	    x2 = Math.abs(x2) > thresh ? x2 : 0.0;
	    

	    vel = -(Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2)));
	    rot = x2;

	    if (Math.abs(vel) > 0.1)
	    {
	        theta = RobotMath.rTD(Math.atan2(y1, x1)) + _gyr.getAngle();
	    }
	    else
	        vel = 0;

	    xVel = vel * Math.cos(RobotMath.dTR(theta));
	    yVel = vel * Math.sin(RobotMath.dTR(theta));

	    spdL = Math.sqrt(Math.pow(xVel + (rot * yPosL), 2) + Math.pow(yVel - (rot * xPosL), 2));
	    angL = RobotMath.rTD(Math.atan2(yVel + (rot * xPosL), xVel - (rot * yPosL)));

	    spdB = Math.sqrt(Math.pow(xVel + (rot * yPosB), 2) + Math.pow(yVel - (rot * xPosB), 2));
	    angB = RobotMath.rTD(Math.atan2(yVel + (rot * xPosB), xVel - (rot * yPosB)));

	    spdR = Math.sqrt(Math.pow(xVel + (rot * yPosR), 2) + Math.pow(yVel - (rot * xPosR), 2));
	    angR = RobotMath.rTD(Math.atan2(yVel + (rot * xPosR), xVel - (rot * yPosR)));

	    Pair<Double, Double> r = optimizeSwerve(frontLEncoder.getAngle(), angR, spdR);
	    Pair<Double, Double> l = optimizeSwerve(frontREncoder.getAngle(), angL, spdL);
	    Pair<Double, Double> b = optimizeSwerve(backEncoder.getAngle(), angB, spdB);

	    _rotFR.setControlTarget(r.left +(x1 == 0 && x2 != 0 ? 0.1 : 0));
	    _rotFL.setControlTarget(l.left +(x1 == 0 && x2 != 0 ? 0.1 : 0));
	    _rotBk.setControlTarget(b.left +(x1 == 0 && x2 != 0 ? 0.1 : 0));

	    if (Math.abs(r.right) > 1 || Math.abs(l.right) > 1 || Math.abs(b.right) > 1)
	    {
	        double scl = Math.max(Math.abs(r.right), Math.max(Math.abs(l.right), Math.abs(b.right)));
	        r.right /= scl;
	        l.right /= scl;
	        b.right /= scl;
	    }
	    _drvFR.setControlTarget(r.right);
	    _drvFL.setControlTarget(-l.right);
	    _drvBk.setControlTarget(b.right);
	}


}
