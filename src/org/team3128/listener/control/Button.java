package org.team3128.listener.control;

/**
 * Object to represent a button of a joystick.  It can either be for an up state or a down state.
 * Its code represents its place in the joystick it is for. 
 * It implements hashCode() and equals(), so it can be used as a HashMap key. 
 * @author Jamie
 *
 */
public class Button implements IControl
{
	int _code;
	
	boolean _up;
	
	public int getCode()
	{
		return _code;
	}

	public boolean isUp()
	{
		return _up;
	}
	
	
	public Button(int code, boolean up)
	{
		_code = code;
		_up = up;
	}
	
	@Override
	public int hashCode()
	{
		int hashCode = _code + 1;
		hashCode *= 37 * (_up ? 1 : 0);
		return hashCode;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Button)
		{
			Button button = (Button)object;
			if(_up == button._up)
			{
				if(_code == button._code)
				{
					return true;
				}
			}
		}
		
		return false;
	}
}
