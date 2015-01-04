package org.team3128;

import org.team3128.drive.ArcadeDrive;
import org.team3128.hardware.encoder.MagneticPotentiometerEncoder;
import org.team3128.hardware.misc.CockArm;
import org.team3128.hardware.misc.GyroLink;
import org.team3128.hardware.misc.Lights;
import org.team3128.hardware.misc.RelayLink;
import org.team3128.hardware.misc.TachLink;
import org.team3128.hardware.motor.MotorLink;
import org.team3128.hardware.motor.speedcontrol.LinearAngleTarget;
import org.team3128.listener.IListenerCallback;
import org.team3128.listener.Listenable;
import org.team3128.listener.ListenerManager;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;

/**
 * The Global class is where all of the hardware objects that represent the robot are stored.
 * It also sets up the control bindings.
 * 
 * Its functions are called only by RobotTemplate.
 * @author Jamie
 *
 */
public class Global
{
	GyroLink _gyr;
	DigitalInput _shooterTSensor;
    DigitalInput _ballTSensor0;
    DigitalInput _ballTSensor1;

	
	MagneticPotentiometerEncoder _encFr;
	MagneticPotentiometerEncoder _encFl;
	MagneticPotentiometerEncoder _encBk;

    MotorLink _rotFr;
    MotorLink _rotFl;
    MotorLink _rotBk;
	MotorLink _drvFr;
	MotorLink _drvFl;
	MotorLink _drvBk;
	MotorLink _mShooter;
	MotorLink _mArmRoll;
	MotorLink _mArmMove;
	public CockArm _cockArm;
	Lights _lights;
	RelayLink _camLights;
	public ListenerManager _listenerManager;
	ArcadeDrive _arcadeDrive;
	
	public Global()
	{
		_encFr = new MagneticPotentiometerEncoder(-60, 1, 2);
		_encFl = new MagneticPotentiometerEncoder(17, 1, 3);
		_encBk = new MagneticPotentiometerEncoder(-35, 1, 4);		
		
		_rotFr = new MotorLink(new Talon(2), _encFr, new LinearAngleTarget(.40, 4)); //OFFSET: -55 DEG
		_rotFl = new MotorLink(new Talon(2), _encFl, new LinearAngleTarget(.40, 4)); //OFFSET: -18 DEG
		_rotBk = new MotorLink(new Talon(2), _encBk, new LinearAngleTarget(.40, 4)); //OFFSET: -10 DEG
		
		_drvFr = new MotorLink(new Talon(2));
		_drvFl = new MotorLink(new Talon(2));
		_drvBk = new MotorLink(new Talon(2));
		
		_mShooter = new MotorLink(new Talon(6));
		_mArmRoll = new MotorLink(new Talon(6));
		_mArmMove = new MotorLink(new Talon(6));	
		
		_lights = new Lights(new RelayLink(new Relay(4)),new RelayLink(new Relay(4)));
		_camLights = new RelayLink(new Relay(4));
		
		_shooterTSensor = new DigitalInput(1);
		_ballTSensor0 = new DigitalInput(1);
		_ballTSensor1 = new DigitalInput(1);
		
		_gyr = new GyroLink(new Gyro(1));		
		_listenerManager = new ListenerManager(new Joystick(Options.instance()._controllerPort));
		_arcadeDrive = new ArcadeDrive(_drvFl, _drvFr, _listenerManager);
		
		_cockArm = new CockArm(_shooterTSensor, _mShooter);

	}

	void initializeRobot()
	{
		
		TachLink link = new TachLink(0, 54);
		
		Log.debug("Global", "Tachometer: " + link.getRaw());
		
		_lights.lightChange(null);
		
	    _rotBk.startControl(90);
	    _rotFl.startControl(90);
	    _rotFr.startControl(90);
	}

	void initializeDisabled()
	{
		_lights.lightChange(null);
	}

	void initializeAuto()
	{
		new Thread(() -> AutoConfig.initialize(this), "AutoConfig").start();
	}

	void initializeTeleop()
	{
	    _rotBk.startControl(90);
	    _rotFl.startControl(90);
	    _rotFr.startControl(90);

		_lights.lightChange(null);
		
		_listenerManager.addListener(Listenable.YDOWN, () -> _gyr.resetAngle());
        _listenerManager.addListener(Listenable.XDOWN, new IListenerCallback() {
            public void listenerCallback() {
                _cockArm.cancel();
                _mShooter.setSpeed(-1.0);
            }
        });
        _listenerManager.addListener(Listenable.XUP, new IListenerCallback() {
            public void listenerCallback() {
                _cockArm.start();
                _mShooter.setSpeed(0);
            }
        });
        _listenerManager.addListener(Listenable.BDOWN, new IListenerCallback() {
            public void listenerCallback() {
                _cockArm.cancel();
                if(_ballTSensor0.get() || _ballTSensor1.get())
                    _mShooter.setSpeed(1);
            }
        });
        _listenerManager.addListener(Listenable.BUP, new IListenerCallback() {
            public void listenerCallback() {
                _cockArm.start();
                _mShooter.setSpeed(0);
            }
        });
        
        _listenerManager.addListener(Listenable.BACKDOWN, () -> _cockArm.stopArmCock());
        
        _listenerManager.addListener(Listenable.STARTDOWN, () -> _cockArm.startArmCock());

        _listenerManager.addListener(Listenable.JOY2Y, new IListenerCallback() {
            public void listenerCallback() {
                double trgrs = _listenerManager.getRawDouble(Listenable.JOY2Y);
                trgrs = Math.abs(trgrs) > 0.1 ? trgrs : 0;
                _mArmRoll.setSpeed(-trgrs);
            }
        }); 
        
        _listenerManager.addListener(Listenable.BDOWN, () -> _mArmMove.setSpeed(-0.45));
        _listenerManager.addListener(Listenable.BUP, () -> _mArmMove.setSpeed(0));
        _listenerManager.addListener(Listenable.YDOWN, () -> _mArmMove.setSpeed(0.45));
        _listenerManager.addListener(Listenable.YUP, () -> _mArmMove.setSpeed(0));
        _listenerManager.addListener(Listenable.LBDOWN, () -> _lights.lightChange(Options.Alliance.BLUE));
        _listenerManager.addListener(Listenable.RBDOWN, () -> _lights.lightChange(Options.Alliance.RED));


	    _listenerManager.addListener(Listenable.JOY1X, _arcadeDrive::steer);
	    _listenerManager.addListener(Listenable.JOY1Y, _arcadeDrive::steer);
	}
}
