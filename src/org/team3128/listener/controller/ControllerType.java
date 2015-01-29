package org.team3128.listener.controller;

public interface ControllerType
{
	//return the zero-indexed button index that indicates the controller's highest-value button
	public int getMaxButtonValue();
	
	//return the zero-indexed axis index that indicates the controller's highest-value axis
	public int getMaxJoystickValue();
}
