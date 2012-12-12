/**
 * (C) 2012 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * File Id: $Id$
 * Author: Tran Huynh
 * Updated by: $Author$ 
 * Last modified: $DateTime$ 
 * Recent Change List: $Change$
 */
package provision.util;

/**
 * A data structure for storing key-value pair where key and value can belong to any type
 * 
 * @author trhuynh
 *
 */
public class KeyValuePair<K,V>{

	private K key;
	private V value;
	
	public KeyValuePair(K key, V value){
		this.key = key;
		this.value = value;
	}
	
	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}
}
