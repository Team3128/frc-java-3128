package org.team3128.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * This class combines the functions of XControl and ListenerManager from the
 * old robot code. It is constructed with one Joystick object, which,
 * confusingly, seems to be the wpilib metaphor for an entire controller. It
 * polls the controller at a set interval, and invokes listeners whenever a
 * control they're attached to has changed (the button listeners are set for
 * either up or down). You may register the same instance of a listener object for as
 * many controls as you like, but it will only be invoked once per polling cycle
 * no matter how many of its registered controls have changed. However, if you
 * register two different instances of the same listener class for two
 * different controls, those listeners will both be invoked if both controls
 * change.
 * 
 * @author Jamie
 *
 */
public class ListenerManager
{

	// when this is locked no one should touch _joystickValues or _buttonValues
	private ReentrantLock _controlValuesMutex;

	// maps the listeners to the control inputs
	private SynchronizedMultimap<IControl, IListenerCallback> _listeners;

	// wpilib object which represents a controller
	private ArrayList<Joystick> _joysticks;

	private HashMap<Axis, Double> _joystickValues;

	private Set<Button> _buttonValues;

	private ControllerType _controlType;
	
	//used for buddy-box syle joystick precedence, to determine if a joystick axis is being used.
	private static final double JOYSTICK_DEADZONE = .15;

	/**
	 * Construct a ListenerManager from joysticks and their type
	 * @param controlType
	 * @param joysticks The joystick or joysticks to pull data from.
	 *   If multiple joysticks are provided, their inputs will be combined additively. That is, if one person presses A and the other presses B, both A and B's listeners will be triggered.
	 *   For axes, whichever value is outside the joystick deadzone will be used.  If both axes are in use, the joystick specified first in the arguments will take precedence.
	 */
	public ListenerManager(ControllerType controlType, Joystick... joysticks)
	{
		_controlValuesMutex = new ReentrantLock();
		_listeners = new SynchronizedMultimap<IControl, IListenerCallback>();
		_joysticks = new ArrayList<>();
		Collections.addAll(_joysticks, joysticks);
		_controlType = controlType;

		Pair<Set<Button>, HashMap<Axis, Double>> controlValues = pollAllJoysticks();

		_joystickValues = controlValues.right;
		_buttonValues = controlValues.left;
	}

	/**
	 * Add a listener for the given listenable. Multiple listeners can be added
	 * for the same listenable.
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
	 * Note that removeAllListenersForControl(SomeController.FOOUP) is
	 * <b>not</b> the same as
	 * removeAllListenersForControl(SomeController.FOODOWN).
	 * 
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
	 * 
	 * @param button
	 * @return
	 */
	public boolean getRawBool(Button button)
	{
		_controlValuesMutex.lock();
		boolean retval;

		retval = _buttonValues.contains(button);
		_controlValuesMutex.unlock();
		
		return retval;
	}

	/**
	 * Get the raw value for an axis.
	 * 
	 * @param axis
	 * @return
	 */
	public double getRawAxis(Axis axis)
	{
		_controlValuesMutex.lock();
		Double retval = 0.0;

		retval = _joystickValues.get(axis);

		if (retval == null)
		{
			Log.recoverable(
					"ListenerManager",
					"Attempt to read data from ListenerManager whose thread has not finished starting");
			return 0.0;
		}
		_controlValuesMutex.unlock();
		return retval;
	}
	
	/**
	 * Collect control information from all joysticks.
	 * @return
	 */
	Pair<Set<Button>, HashMap<Axis, Double>> pollAllJoysticks()
	{
		Set<Button> netButtonValues = new HashSet<>();
		HashMap<Axis, Double> netJoystickValues = new HashMap<Axis, Double>();
		
		_controlValuesMutex.lock();




		for(int index = _joysticks.size() - 1; index >= 0; --index)
		{
			Joystick currentJoystick = _joysticks.get(index);			
			// read button values
			for (int counter = 1; counter <= _controlType.getMaxButtonValue(); counter++)
			{
				boolean buttonValue = currentJoystick.getRawButton(counter);

				if(buttonValue)
				{
					netButtonValues.add(new Button(counter, false));
				}
			}

			// read joystick values
			for (int counter = 0; counter <= _controlType.getMaxJoystickValue(); counter++)
			{
				Axis currAxis = new Axis(counter);
				double thisJoystickValue = currentJoystick.getRawAxis(counter);
				if((!netJoystickValues.containsKey(currAxis)) || Math.abs(thisJoystickValue) > JOYSTICK_DEADZONE)
				{
					netJoystickValues.put(currAxis, thisJoystickValue);
				}
			}
		}
		_controlValuesMutex.unlock();
		
		return new Pair<Set<Button>, HashMap<Axis, Double>>(netButtonValues, netJoystickValues);

	}
	
	private void addListenersForControl(Set<IListenerCallback> listeners, IControl control)
	{
		// get all its registered listeners
		Collection<IListenerCallback> foundListeners = _listeners.get(control);

		if (foundListeners != null && !foundListeners.isEmpty())
		{
			// loop through them
			for (IListenerCallback callback : foundListeners)
			{
				listeners.add(callback);
			}
		}

	}

	/**
	 * Read controls and update listeners. Usually called by RobotTemplate when
	 * WPILib ticks it.
	 */
	public void tick()
	{
		Pair<Set<Button>, HashMap<Axis, Double>> newValues = pollAllJoysticks();
		Set<IListenerCallback> listenersToInvoke = new HashSet<IListenerCallback>();

		// add ALWAYS listenable
		Collection<IListenerCallback> foundAlwaysListeners = _listeners
				.get(Always.instance);

		if (foundAlwaysListeners != null && !foundAlwaysListeners.isEmpty())
		{
			// loop through them
			for (IListenerCallback callback : foundAlwaysListeners)
			{
				listenersToInvoke.add(callback);
			}
		}
		
		//don't need to lock _controlValuesMutex here because we know it's not going to be modified, because we're the only ones modifying it

		//check for pressed buttons
		for(Button button : newValues.left)
		{
			if(!_buttonValues.contains(button))
			{
				addListenersForControl(listenersToInvoke, new Button(button.getCode(), true));
			}
		}
		
		//check for unpressed buttons
		for(Button button : _buttonValues)
		{
			if(!newValues.left.contains(button))
			{
				addListenersForControl(listenersToInvoke, new Button(button.getCode(), false));
			}
		}

		// loop through joystick values
		for (Axis axis : newValues.right.keySet())
		{
			// has this particular value changed?
			if (Math.abs(_joystickValues.get(axis) - newValues.right.get(axis)) > .0001)
			{
				addListenersForControl(listenersToInvoke, axis);

			}
		}

		// update class variables to match new data
		{
			_controlValuesMutex.lock();
			_buttonValues = newValues.left;
			_joystickValues = newValues.right;
			_controlValuesMutex.unlock();
		}

		// invoke handlers
		for (IListenerCallback listener : listenersToInvoke)
		{
			try
			{
				listener.listenerCallback();
			} catch (RuntimeException error)
			{
				Log.recoverable(
						"ListenerManager",
						"Caught a " + error.toString()
								+ " from a control listener: "
								+ error.getMessage());
				error.printStackTrace();
			}
		}

	}

}
