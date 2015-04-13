package org.team3128.util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Our homegrown replacement for a Guava multimap.
 * 
 * Currently, this is needed to make the ListenerManager work.
 * @author Jamie
 *
 * @param <Key>
 * @param <Value>
 */
public class SynchronizedMultimap<Key, Value>
{
	ConcurrentHashMap<Key, ArrayList<Value>> _map;
	
	public SynchronizedMultimap()
	{
		_map = new ConcurrentHashMap<Key, ArrayList<Value>>();
	}
	
	/**
	 * insert a(nother) value for a key
	 * @param key
	 * @param value
	 */
	public void put(Key key, Value value)
	{
		ArrayList<Value> prevValues = _map.get(key);
		if(prevValues == null)
		{
			prevValues = new ArrayList<Value>();
		}
		
		prevValues.add(value);
		_map.put(key, prevValues);
	}
	
	/**
	 * remove all keys and values from the multimap
	 */
	public void clear()
	{
		_map.clear();
	}
	
	/**
	 * remove the key and all of its values from the map
	 */
	public void removeAll(Key key)
	{
		_map.remove(key);
	}
	
	/**
	 * get a list of values for a key
	 * @param key
	 * @return
	 */
	public ArrayList<Value> get(Key key)
	{
		return _map.get(key);
	}
	
	
}	
