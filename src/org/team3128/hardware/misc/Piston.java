package org.team3128.hardware.misc;

import org.team3128.Log;

import edu.wpi.first.wpilibj.Solenoid;


/**
 * Class to control a pneumatic piston, which has a two solenoids which
 * control its position.
 * @author Noah Sutton-Smolin
 */
public class Piston 
{
    private final Solenoid solA, solB;
    private boolean isInverted = false;
   
    public Piston(Solenoid solA, Solenoid solB)
    {
        this.solA = solA; this.solB = solB;
    }
   
    public Piston(Solenoid solA, Solenoid solB, boolean solStateA, boolean solStateB)
    {
        this(solA, solB);
        solA.set(solStateA); solB.set(solStateB);
    }
   
    public void invertPiston() {this.isInverted = !isInverted;}
   
    public void lockPiston()
    {
        solA.set(true);
        solB.set(true);
        Log.debug("Piston"," set to locked state");
    }

    public void unlockPiston() 
    {
        solA.set(false);
        solB.set(false);
        Log.debug("Piston", " set to unlocked state");
    }
   
    public void setPistonOn()
    {
        solA.set(true ^ this.isInverted);
        solB.set(false ^ this.isInverted);
        Log.debug("Piston", " set to on state");
    }
   
    public void setPistonOff() 
    {
        solA.set(false ^ this.isInverted);
        solB.set(true ^ this.isInverted);
        //Log.debug("Piston", " set to off state");
    }
   
    /**
     * Swap the piston's current position
     */
    public void setPistonInvert()
    {
        solA.set(!solA.get());
        solB.set(!solB.get());
        Log.debug("Piston", " set to flip-state " + solA.get() + ", " +solB.get());
    }

}