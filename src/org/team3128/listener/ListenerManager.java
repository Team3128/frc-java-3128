package org.team3128.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.team3128.Log;
import org.team3128.listener.control.Always;
import org.team3128.listener.control.Axis;
import org.team3128.listener.control.Button;
import org.team3128.listener.control.IControl;
import org.team3128.listener.controller.ControllerType;
import org.team3128.util.Pair;
import org.team3128.util.SynchronizedMultimap;

import edu.wpi.first.wpilibj.Joystick;

/**
 *This class combines the functions of XControl and ListenerManager from the old robot code.
 *It is constructed with one Joystick object, which, confusingly, seems to be the wpilib metaphor for an entire controller.
 *It polls the controller at a set interval, and invokes listeners
 *whenever a value they're set for has changed (the button listeners are set for either up or down).
 *Listeners are run on the object's polling thread, and will need to be passed a reference
 *to the listener manager somehow if they need control data (or they can use lambda capture).
 *You may register the same instance of a listener for as many
 *controls as you like, but it will only be invoked once per polling cycle no matter how many of its registered
 *controls have changed.  However, if you register two different instances of the same listener object for two different controls, 
 *those listeners will both be invoked if both controls change. 
 * 
 * @author Jamie
 *
 */
public class ListenerManager
{
	
	//when this is locked no one should touch _joystickValues or _buttonValues
	ReentrantLock _controlValuesMutex;
	
	//maps the listeners to the control inputs
	SynchronizedMultimap<IControl, IListenerCallback> _listeners;
	
	//wpilib object which represents a controller
	Joystick _joystick;

	HashMap<Axis, Double> _joystickValues;

	HashMap<Button, Boolean> _buttonValues;
	
	ControllerType _controlType;
	
	
	//construct from existing Joystick
	public ListenerManager(Joystick joystick, ControllerType controlType)
	{
		_controlValuesMutex = new ReentrantLock();
		_listeners = new SynchronizedMultimap<IControl, IListenerCallback>();
        _joystick = joystick;
        _controlType = controlType;
        
        Pair<HashMap<Button, Boolean>, HashMap<Axis, Double>> controlValues = pollControls();
        
		_joystickValues = controlValues.right;
		_buttonValues = controlValues.left;
	}
	
	/**
	 * Add a listener for the given listenable.
	 * Multiple listeners can be added for the same listenable.
	 * 
	 * @param key
	 * @param listener
	 */
	public void addListener(IControl key, IListenerCallback listener)
	{
		_listeners.put(key, listener);
	}

	/**
	 * remove all listeners, period
	 */
	public void removeAllListeners()
	{
		_listeners.clear();
	}

	/**
	 * Remove all listeners set for the given listener. <br>
	 * Note that removeAllListenersForControl(SomeController.FOOUP) is <b>not</b> the same as removeAllListenersForControl(SomeController.FOODOWN).
	 * @param listener
	 */
	public void removeAllListenersForControl(IControl listener)
	{
		_listeners.removeAll(listener);
	}

	//
	//
	//
	
	/**
	 * Returns the boolean value of a button listenable (between A and R3).
	 * Returns true if the button is pressed whether FOOUP or FOODOWN was given.
	 * Does bounds checking, throws if value is out of range.
	 * @param button
	 * @return
	 */
	public boolean getRawBool(Button button)
	{
		_controlValuesMutex.lock();
		Boolean retval = false;
		
		retval = _buttonValues.get(button);
		_controlValuesMutex.unlock();

		if(retval == null)
		{
			Log.recoverable("ListenerManager", "Attempt to read data from ListenerManager whose thread has not finished starting");
			return false;
		}
		
		return retval;
	}

	/**
	 * Get the raw value for an axis.
	 * @param axis
	 * @return
	 */
	public double getRawAxis(Axis axis)
	{
		_controlValuesMutex.lock();
		Double retval = 0.0;
		
		retval = _joystickValues.get(axis);
		
		if(retval == null)
		{
			Log.recoverable("ListenerManager", "Attempt to read data from ListenerManager whose thread has not finished starting");
			return 0.0;
		}
		_controlValuesMutex.unlock();
		return retval;
	}

	/**
	 * 
	 * @return two HashMaps with the current button and axis values for the controller
	 */
	Pair<HashMap<Button, Boolean>, HashMap<Axis, Double>> pollControls()
	{
		HashMap<Button, Boolean> buttonValues = new HashMap<Button, Boolean>();
		HashMap<Axis, Double> joystickValues = new HashMap<Axis, Double>();
	
		_controlValuesMutex.lock();

		//read button values
		for(int counter = 1; counter <= _controlType.getMaxButtonValue() ; counter++)
		{
			boolean buttonValue = _joystick.getRawButton(counter);
			
			Button buttonToPut = new Button(counter, false);
			
			buttonValues.put(buttonToPut, buttonValue);
		}
				
		//read joystick values
		for(int counter = 0; counter <= _controlType.getMaxJoystickValue(); counter++)
		{
			//round to 3 decimal places
			joystickValues.put(new Axis(counter), Math.round(_joystick.getRawAxis(counter) * 1000.0) / 1000.0);
		}
				
		_controlValuesMutex.unlock();
		
		return new Pair<HashMap<Button, Boolean>, HashMap<Axis, Double>>(buttonValues, joystickValues);
	}

	/**
	 * Read controls and update listeners.  Usually called by RobotTemplate when WPILib ticks it.
	 */
	public void tick()
	{
		Pair<HashMap<Button, Boolean>, HashMap<Axis, Double>> newValues = pollControls();
		Set<IListenerCallback> listenersToInvoke = new HashSet<IListenerCallback>();
		
		//add ALWAYS listenable
		Collection<IListenerCallback> foundAlwaysListeners = _listeners.get(Always.instance);

		if(foundAlwaysListeners != null && !foundAlwaysListeners.isEmpty())
		{
			//loop through them
			for(IListenerCallback callback : foundAlwaysListeners)
			{
				listenersToInvoke.add(callback);
			}
		}

		//loop through button values
		for(Button button : newValues.left.keySet())
		{
			//has this button been pressed?
			Boolean oldValue = _buttonValues.get(button);
			Boolean newValue = newValues.left.get(button);
			if(!oldValue && newValue)
			{
				//get all its registered listeners
				Collection<IListenerCallback> foundListeners = _listeners.get(new Button(button.getCode(), false));

				if(foundListeners != null && !foundListeners.isEmpty())
				{
					//loop through them
					for(IListenerCallback callback : foundListeners)
					{
						listenersToInvoke.add(callback);
					}
				}

			}

			//loop through button up values
			//has this button just stopped being pressed?
			if(oldValue && !newValue)
			{
				//get all its registered listeners
				Collection<IListenerCallback> foundListeners = _listeners.get(new Button(button.getCode(), true));

				if(foundListeners != null && !foundListeners.isEmpty())
				{
					//loop through them
					for(IListenerCallback callback : foundListeners)
					{
						listenersToInvoke.add(callback);
					}
				}

			}
		}


		//loop through joystick values
		for(Axis axis : newValues.right.keySet())
		{
			//has this particular value changed?
			if(Math.abs(_joystickValues.get(axis) - newValues.right.get(axis)) > .0001)
			{
				//get all its registered listeners
				Collection<IListenerCallback> foundListeners = _listeners.get(axis);

				if(foundListeners != null && !foundListeners.isEmpty())
				{
					//loop through them
					for(IListenerCallback callback : foundListeners)
					{
						listenersToInvoke.add(callback);
					}
				}

			}
		}


		//update class variables to match new data
		{
			_controlValuesMutex.lock();
			_buttonValues = newValues.left;
			_joystickValues = newValues.right;
			_controlValuesMutex.unlock();
		}

		//invoke handlers
		for(IListenerCallback listener: listenersToInvoke)
		{
			try
			{
				listener.listenerCallback();
			}
			catch(RuntimeException error)
			{
				Log.recoverable("ListenerManager", "Caught a " + error.toString() + " from a control listener: " + error.getMessage());
				error.printStackTrace();
			}
		}
		
	}

}
