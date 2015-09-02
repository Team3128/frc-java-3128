 package org.team3128.hardware.motor;

import java.util.HashSet;

import org.team3128.RobotProperties;

/**
* MotorLogic is an abstract superclass for classes that define the behavior of a motor.  
* This class does all of the boilerplate, like getters and setters, while the subclass
* implements the actual math and logic.
* @author Noah Sutton-Smolin
*/
public abstract class MotorLogic
{
	//owned by the control thread
	private long lastRuntime = 0;
   
	private MotorGroup controlledMotor = null;
	private Thread thread;
	
	private HashSet<Limiter> limiters;
	
	protected int _refreshTime = RobotProperties.motorControlUpdateFrequency;
		
	public MotorLogic()
	{
		limiters = new HashSet<Limiter>();
	}

	protected synchronized void setControlledMotor(MotorGroup m)
	{
		controlledMotor = m;
	}

	//please make sure to make these two voids synchronized when overriding them.
   /**
    * Set the desired value for the motor controller to target.
    * Exactly what units this is in depends on the controller.
    * @param val
    */
   public abstract void setControlTarget(double val);
   
   /**
    * Reset any permanent state of the speed controller as well as the speed target.
    */
   public abstract void clearControlRun();
   
   /**
    * Update the speed control.
    * @param dt the time im milliseconds since the last update.  Actually working since 4-6-2015!
    * @return
    */
   public abstract double speedControlStep(double dt);
   
   /**
    * Return true if the motor control is finished and the thread should stop running.
    */
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
    * Add the limiter for the speed control to use. <br>
    * @param limiter
    */
   public void addLimiter(Limiter limiter)
   {
	   limiters.add(limiter);
   }
   
   /**
    * Reset all limiters.
    * @param limiter
    */
   public void resetAllLimiters()
   {
	   for(Limiter limiter : limiters)
	   {
		   limiter.reset();
	   }
   }
   
   /**
    * Remove a limiter from the list of limiters.
    * @param limiter
    */
   public void removeLimiter(Limiter limiter)
   {
	   limiters.remove(limiter);
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
    * Sets the speed update time in msec
    *
    * @param refreshTime time between updates in msec
    */
   public void setRefreshTime(int refreshTime)
   {
	   _refreshTime = refreshTime;
   }

   /**
    * internal thread
    */
   public final void run() 
   {
	   while(true)
	   {
		   synchronized(this)
		   {
		       if(this.isComplete())
		       {
		           this.controlledMotor.setInternalSpeed(0);
		           clearControlRun();
		           return;
		       }
		       else
		       {
		    	   double newSpeed = speedControlStep(getLastRuntimeDist());
		    	   for(Limiter currentLimiter : limiters)
		    	   {
			    	   if(!currentLimiter.canMove(newSpeed))
			    	   {
			    		   newSpeed = 0;
			    	   }
		    	   }

		    	   controlledMotor.setInternalSpeed(newSpeed);
		       }
		   }
		   
	       lastRuntime = System.currentTimeMillis();
	       
		   try
		   {
			   Thread.sleep(_refreshTime);
		   }
		   catch (InterruptedException e)
		   {
			   return;
		   }
	   }
   }
   
   public final void shutDown()
   {
	   if(thread != null)
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
	   
   }
   
   /**
    * Start the controller thread if it is stopped, which it is when you construct the object.
    */
   public void start()
   {
	   if(thread == null || !thread.isAlive())
	   {
		   thread = new Thread(this::run, "MotorLogic Thread");
		   thread.start();
	   }
   }
   
   public boolean isRunning()
   {
	   if(thread == null)
	   {
		   return false;
	   }
	   
	   return thread.isAlive();
   }
   
}

