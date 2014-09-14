package org.team3128.hardware.misc;

import org.team3128.Log;

import robotemulator.Solenoid;


/**
 *
 * @author Noah Sutton-Smolin
 */
public class Piston {
    private final Solenoid solA, solB;
    private boolean inversion = false;
   
    public Piston(Solenoid solA, Solenoid solB) {
        this.solA = solA; this.solB = solB;
    }
   
    public Piston(Solenoid solA, Solenoid solB, boolean solStateA, boolean solStateB) {
        this(solA, solB);
        solA.set(solStateA); solB.set(solStateB);
    }
   
    public void invertPiston() {this.inversion = !inversion;}
   
    public void lockPiston() {
        solA.set(true);
        solB.set(true);
        Log.debug("Piston"," set to locked state");
    }

    public void unlockPiston() {
        solA.set(false);
        solB.set(false);
        Log.debug("Piston", " set to unlocked state");
    }
   
    public void setPistonOn() {
        solA.set(true ^ this.inversion);
        solB.set(false ^ this.inversion);
        Log.debug("Piston", " set to on state");
    }
   
    public void setPistonOff() {
        solA.set(false ^ this.inversion);
        solB.set(true ^ this.inversion);
        Log.debug("Piston", " set to off state");
    }
   
    public void setPistonInvert() {
        solA.set(!solA.get());
        solB.set(!solB.get());
        Log.debug("Piston", " set to flip-state " + solA.get() + ", " +solB.get());
    }

}