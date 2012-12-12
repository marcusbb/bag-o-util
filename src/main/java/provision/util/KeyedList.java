/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/KeyedList.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import provision.services.security.AclPermission;
//import provision.services.security.ref.AclPermissions;
import provision.util.beans.BeanManager;
import provision.util.beans.KeyMethod;

/**
 * This is a list that can be accessed like a Map.
 * 
 * @author Iulian Vlasov
 */
@SuppressWarnings("serial")
public class KeyedList<T> extends AbstractList<T> implements Serializable {

	static final int DEFAULT_CAPACITY = 1;

	private Class<T> itemClass;
	private Class<?> keyClass;
	private String keyPropName;
	private List<T> items;
	private Map<Object, T> itemsByKey;
	private transient Method keyGetter;
	private int capacity = DEFAULT_CAPACITY;

	public KeyedList() {
	}

	public KeyedList(String keyPropName) {
		this.keyPropName = keyPropName;
	}

	public KeyedList(List<T> itemList) {
		this(itemList, null, null);
	}

	public KeyedList(List<T> itemList, String keyPropName) {
		this(itemList, keyPropName, null);
	}

	public KeyedList(List<T> itemList, Class<T> itemClass) {
		this(itemList, null, itemClass);
	}

	@SuppressWarnings("unchecked")
	public KeyedList(List<T> itemList, String keyPropName, Class<T> itemClass) {
		this.items = itemList;
		this.keyPropName = keyPropName;
		this.itemClass = (Class<T>) (itemClass == null ? itemList.get(0).getClass() : itemClass);
		this.capacity = itemList.size();

		findKeyGetter();
		prepareKeys();
	}

	public KeyedList(int capacity) {
		this.capacity = capacity;
	}

	public KeyedList(Class<T> itemClass) {
		this.itemClass = itemClass;
		findKeyGetter();
	}

	public KeyedList(Class<T> itemClass, int capacity) {
		this(itemClass);
		this.capacity = capacity;
	}

	/**
	 * Return the key of the object at the specified index.
	 * 
	 * @param index
	 * @return
	 */
	public Object getKey(int index) {
		T item = get(index);
		return (item == null ? null : getKey(item));
	}

	/**
	 * Return and iterator over the keys of the items in the list.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Iterator<Object> names() {
		if (itemsByKey == null) {
			return Collections.EMPTY_SET.iterator();
		}
		else {
			return itemsByKey.keySet().iterator();
		}
	}

	/**
	 * Return the object with the specified key.
	 * 
	 * @param key
	 * @return
	 */
	public T get(Object key) {
		prepareKeys();
		return (itemsByKey == null ? null : itemsByKey.get(key));
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (items != null) {
			for (T item : items) {
				sb.append(StringUtil.NEWLINE).append(getKey(item)).append('=').append(item.getClass().getName());
				sb.append(StringUtil.NEWLINE).append(item);
			}
		}

		return sb.toString();
	}

	protected void prepareKeys() {
		if ((itemsByKey == null) && !SystemUtil.isEmpty(items)) {
			for (T item : items) {
				addKey(item);
			}
		}
	}

	protected void addKey(T item) {
		Object key = getKey(item);
		addKey(key, item);
	}

	protected void addKey(Object key, T item) {
		if (key != null) {
			if (itemsByKey == null) {
				itemsByKey = new HashMap<Object, T>(capacity, 1);
			}

			itemsByKey.put(key, item);
		}
	}

	@SuppressWarnings("unchecked")
	protected Object getKey(T item) {
		Object key = null;
		try {
			if (keyGetter == null) {
				this.itemClass = (Class<T>) item.getClass();
				findKeyGetter();
			}
			
			if (keyGetter.getDeclaringClass().isAssignableFrom(item.getClass())) {
				key = (Object) keyGetter.invoke(item);
			}
			else if (keyPropName != null) {
				key = BeanManager.getProperty(item, keyPropName);
			}
			else {
				throw new IllegalArgumentException(item.toString());
			}
		}
		catch (Exception e) {
			// what can I do now?
			throw new IllegalStateException("Unable to retrieve the key for this item: " + item, e);
		}

		return key;
	}

	private void findKeyGetter() {
		try {
			if (SystemUtil.isEmpty(keyPropName)) {
				Method[] methods = itemClass.getMethods();
				for (Method method : methods) {
					if (method.getAnnotation(KeyMethod.class) != null) {
						this.keyGetter = method;
						
						// optional
						String name = keyGetter.getName();
						if (name.startsWith("get") && (name.length() > 3)) {
							StringBuilder sb = new StringBuilder().append(Character.toLowerCase(name.charAt(3)));
							if (name.length() > 4) {
								sb.append(name.substring(4));
							}
							keyPropName = sb.toString();
						}
						break;
					}
				}
			}
			else {
				this.keyGetter = BeanManager.getGetter(itemClass, keyPropName, null);
			}

			if (keyGetter != null) {
				this.keyClass = (Class<?>) keyGetter.getReturnType();
			}
			else {
				throw new NoSuchMethodException(keyPropName);
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/***********************
	 * # List Implementation #
	 ***********************/
	public T get(int index) {
		return (items == null ? null : items.get(index));
	}

	public int size() {
		return (items == null ? 0 : items.size());
	}

	public void clear() {
		if (itemsByKey != null) {
			itemsByKey.clear();
			itemsByKey = null;
		}
		if (items != null) {
			items.clear();
			items = null;
		}
	}

	public boolean add(T item) {
		if (items == null) items = new ArrayList<T>(capacity);

		Object key = getKey(item);
		T oldItem = get(key);

		if (oldItem == null) {
			// add new item
			items.add(item);
		}
		else {
			// replace old item
			int index = items.indexOf(oldItem);
			if (index != -1) {
				items.set(index, item);
			}
			else {
				items.add(item);
			}
		}

		addKey(key, item);

		return true;
	}

	public T remove(int index) {
		if (items == null) return null;

		T item = get(index);
		remove(item);

		return item;
	}

	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		boolean existed = false;

		T item = null;
		Object key = null;

		if (o.getClass().isAssignableFrom(keyClass)) {
			key = (Object) o;
			item = get(key);
		}
		else {
			item = (T) o;
			key = getKey(item);
		}

		if (items != null) {
			existed = items.remove(item);
		}

		if ((itemsByKey != null) && (key != null)) {
			itemsByKey.remove(key);
		}

		return existed;
	}

	public static void main(String[] args) {
		/*
//		KeyedList<AclPermission> l = new KeyedList<AclPermission>("action");
		KeyedList<AclPermission> l = new KeyedList<AclPermission>(AclPermission.class);

		l.add(AclPermissions.activate);
		l.add(AclPermissions.deactivate);

//		List<AclPermission> list = new ArrayList<AclPermission>();
//		list.add(AclPermissions.activate);
//		list.add(AclPermissions.deactivate);
//
////		KeyedList<AclPermission> l = new KeyedList<AclPermission>(list);
//		KeyedList<AclPermission> l = new KeyedList<AclPermission>(list, AclPermission.class);

		System.out.println(l.get(0));
		System.out.println(l.getKey(0));
		System.out.println(l.get("activate"));
		System.out.println(l.get("deactivate"));
		l.get(AclPermissions.activate);
		
//		l.add( new AclPermissionImpl("activate", null, false));

		l.add( new AclPermission() {

			public String getAction() {
				// TODO Auto-generated method stub
				return "activate";
			}

			public String getResource() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean isGranted() {
				// TODO Auto-generated method stub
				return false;
			}
			
		});

		System.out.println(l.get("activate"));
		
		Iterator<Object> keys = l.names();
		while (keys.hasNext()) {
			System.out.println(keys.next());
		}
		*/
	}

}
