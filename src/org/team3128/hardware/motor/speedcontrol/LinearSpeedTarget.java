package org.team3128.hardware.motor.speedcontrol;

import org.team3128.hardware.motor.MotorControl;
import org.team3128.util.RobotMath;

/**
 * Used for computing a target power with a linear shift.
 *
 * @author Noah Sutton-Smolin
 */
public class LinearSpeedTarget extends MotorControl
{
    private double tgtSpeed;
    private double dtAccum;
    private double refreshTime = 50;
    private double lastEncoderAngle = -1;

    /**
     *
     * @param tgtSpeed    target speed in deg/sec
     * @param refreshTime speed update rate in msec
     */
    public LinearSpeedTarget(double tgtSpeed, double refreshTime)
    {
        this.tgtSpeed = tgtSpeed; this.refreshTime = refreshTime/1000.0;
        lastEncoderAngle = this.getLinkedEncoderAngle();
    }
   
    /**
     * Uses a default refreshTime of 50msec
     *
     * @param tgtSpeed target speed in deg/sec
     */
    public LinearSpeedTarget(double tgtSpeed) {this(tgtSpeed, 0.050);}
   
    public void setControlTarget(double d)
    {
        targetLock.lock();
    	tgtSpeed = d;
    	lastEncoderAngle = this.getLinkedEncoderAngle();
        targetLock.unlock();
    }

    public double speedControlStep(double dt) {
        dtAccum += dt; if(dtAccum < refreshTime) return this.getLinkedMotorSpeed();

        // Power * (current rate of change) / (target rate of change) => pow * (deg/sec) / (deg/sec) => pow
        double retVal = RobotMath.makeValidPower((this.getLinkedMotorSpeed() / tgtSpeed) * ((this.getLinkedEncoderAngle()-lastEncoderAngle)/dtAccum));
        dtAccum = 0; lastEncoderAngle = this.getLinkedEncoderAngle(); return retVal;
    }

    /**
     * Sets the speed update time in msec
     *
     * @param refreshTime speed update rate in msec
     */
    public void setRefreshTime(double refreshTime) {this.refreshTime = refreshTime/1000.0;}
   
    public void clearControlRun()
    {
        targetLock.lock();
    	this.tgtSpeed = 0; this.lastEncoderAngle = this.getLinkedEncoderAngle();
        targetLock.unlock();
    }

    public boolean isComplete() {return false;}
}
