package org.team3128.listener.control;

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
		int hashCode = _code;
		_code *= 37 * (_up ? 1 : 0);
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
