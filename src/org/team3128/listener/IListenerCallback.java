package org.team3128.listener;

/**
 * Defines the interface for a listener callback.  There are four ways to make one of these: <br>
 * First, you can have your class extend IListenerCallback.<br>
 * Second, you can make an anonymous IListenerCallback().<br>
 * Third, you can use any class with a no-argument function and pass a method reference, e.g. System.out::println<br>
 * Fourth, you can use a no-argument lambda. 
 * 
 * @author Jamie
 *
 */
public interface IListenerCallback
{
	public void listenerCallback();
}
