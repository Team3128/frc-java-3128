package org.team3128.hardware.motor;

import java.util.concurrent.locks.ReentrantLock;

import org.team3128.Options;

/**
* MotorControl is an abstract superclass for classes that define the behavior of a motor.  
* This class does all of the boilerplate, like getters and setters, while the subclass
* implements the actual math and logic.
* @author Noah Sutton-Smolin
*/
public abstract class MotorControl
{
    //owned by the control thread
	private long lastRuntime = 0;
   
	private MotorLink controlledMotor = null;
	private Thread thread;
	
	protected ReentrantLock targetLock;
	
	public MotorControl()
	{
		targetLock = new ReentrantLock();
	}

   protected void setControlledMotor(MotorLink m)
   {
	   targetLock.lock();
	   controlledMotor = m;
	   targetLock.unlock();
   }

   
   //********************************************************
   //WHEN OVERRIDING THESE TWO FUNCTIONS MAKE SURE TO LOCK THE targetLock LOCK!!!!!!!!!!!!!!!
   //********************************************************
   public abstract void setControlTarget(double val);
   public abstract void clearControlRun();
   
   public abstract double speedControlStep(double dt);
   public abstract boolean isComplete();

   /**
    *
    * @return the last runtime in system clock milliseconds
    */
   public final long getLastRuntime()
   {
	   return lastRuntime;
   }

   /**
    *
    * @return how long ago the event was last called (used for dT)
    */
   public final long getLastRuntimeDist()
   {
	   return System.currentTimeMillis() - lastRuntime;
   }

   /**
    *
    * @return the encoder value of the controlled motor
    */
   public double getLinkedEncoderAngle()
   {
	   return this.controlledMotor.getEncoderAngle();
   }

   /**
    *
    * @return the linked motor's speed
    */
   public double getLinkedMotorSpeed()
   {
	   return this.controlledMotor.getSpeed();
   }

   public final void run() 
   {
	   while(true)
	   {
		   targetLock.lock();
	       lastRuntime = System.currentTimeMillis();
	       if(this.isComplete())
	       {
	           this.controlledMotor.setInternalSpeed(0);
	       }
	       else
	       {
	    	   this.controlledMotor.setInternalSpeed(this.speedControlStep(this.getLastRuntimeDist()));
	       }
	       targetLock.unlock();
	       
	    try
		{
			Thread.sleep(Options.instance()._motorControlUpdateFrequency);
		}
	    catch (InterruptedException e)
		{
			return;
		}
	   }
   }
   
   public void shutDown()
   {
	   thread.interrupt();
	   try
	   {
		   thread.join();
	   } 
	   catch (InterruptedException e)
	   {
			e.printStackTrace();
			return;
	   }
   }
   
   /**
    * Start the controller thread if it is stopped, which it is when you construct the object.
    */
   public void start()
   {
	   if(thread == null || !thread.isAlive())
	   {
		   thread = new Thread(this::run, "MotorControl Thread");
		   thread.start();
	   }
   }
   
   public boolean isRunning()
   {
	   return thread.isAlive();
   }
   
}

