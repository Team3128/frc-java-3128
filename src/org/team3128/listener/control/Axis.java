package org.team3128.listener.control;

public class Axis implements IControl
{
	int _code;
	
	public int getCode()
	{
		return _code;
	}
	
	public Axis(int code)
	{
		_code = code;
	}
	
	@Override
	public int hashCode()
	{
		return _code;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Axis)
		{
			Axis axis = (Axis)object;
			if(_code == axis._code)
			{
				return true;
			}
			
		}
		
		return false;
	}
}
