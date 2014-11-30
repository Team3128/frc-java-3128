package org.team3128.hardware.motor;

import org.team3128.Log;
import org.team3128.hardware.encoder.IEncoder;

import edu.wpi.first.wpilibj.Talon;

/**
 * MotorLink is the class used in this code to represent a motor.
 * It can operate with or without an encoder and with several
 * different varieties of speed control or none at all.
 */
public class MotorLink {
    private final Talon talon;
    private IEncoder encoder;
    private MotorControl spdControl;
    private boolean motorReversed = false;
    private double speedScalar = 1;

    public MotorLink(Talon talon) {this.talon = talon;}
    public MotorLink(Talon talon, double powscl) {this(talon); this.speedScalar = powscl;}
    public MotorLink(Talon talon, IEncoder enc) {this(talon); this.encoder = enc;}
    public MotorLink(Talon talon, IEncoder enc, double powscl) {this(talon, enc); this.speedScalar = powscl;}
    public MotorLink(Talon talon, IEncoder enc, MotorControl spd) {this(talon, enc); this.spdControl = spd;}
    public MotorLink(Talon talon, IEncoder enc, MotorControl spd, double powscl) {this(talon, enc, spd); this.speedScalar = powscl;}

    public void reverseMotor() {motorReversed = !motorReversed;}
    public void setSpeedScalar(double powScl) {this.speedScalar = powScl;}
    protected void setInternalSpeed(double pow) {talon.set(pow * speedScalar * (motorReversed ? -1.0 : 1.0));}
    public void setSpeed(double pow)
    {
        if(this.spdControl != null && spdControl.isRunning())
        {
            this.spdControl.shutDown();
            Log.unusual("MotorLink", "The motor power was set from outside the speed controller, so the controller was canceled.");
        }
        setInternalSpeed(pow);
    }
    public void setEncoder(IEncoder enc) {
        if (this.encoder != null)
            Log.unusual("MotorLink", "The encoder has been changed when one already existed.");
        this.encoder = enc;
    }
    public void setSpeedController(MotorControl spdControl) {
        if(this.spdControl != null && this.spdControl.isRunning()) 
        {
            this.spdControl.shutDown();
            Log.fatal("MotorLink", "The speed controller was changed when one was running.");
        }
        
        this.spdControl = spdControl;
    }

    public double getSpeedScalar(double powScl) {return this.speedScalar;}
    public double getSpeed() {return talon.get();}
    public double getEncoderAngle() {
        if (encoder == null) {
            Log.recoverable("MotorLink", "Something attempted to get the encoder value, but no encoder exists.");
            return -1;
        }        
        
        return encoder.getAngle();
    }

    public void setControlTarget(double target) {
        if (this.spdControl == null) {
            Log.recoverable("MotorLink", "The speed controller's target was set, but none exists.");
            return;
        }
        if(!spdControl.isRunning())
        {
            Log.recoverable("MotorLink", "The speed controller's target was set, but it is not enabled.");
            return;
        }

        this.spdControl.setControlTarget(target);
    }

    public boolean speedControlRunning()
    {
    	return spdControl.isRunning();
    }
    public void setSpeedControlTarget(double target) {this.spdControl.setControlTarget(target);}

    public void startControl(double target)
    {
        this.spdControl.clearControlRun();
        this.spdControl.setControlTarget(target);
        this.spdControl.setControlledMotor(this);
        spdControl.start();
    }

    public void stopSpeedControl()
    {
        spdControl.shutDown();
    }
}

