package org.team3128.util;

/**
 * This class is useful when one wants to return two values from one function
 * I can't understand why it is not in the Java Standard Library 
 * @author Jamie
 *
 */
public class Pair<L, R>
{
	public L left;
	
	public R right;
	
	public Pair(L left, R right)
	{
		this.left = left;
		this.right = right;
	}

	public Pair()
	{

	}
}
