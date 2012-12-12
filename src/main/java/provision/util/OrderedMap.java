/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/OrderedMap.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a map with the ability to iterate the keys in the order they were
 * added to the map.
 * 
 * @param <K>
 * @param <V>
 * @author Iulian Vlasov
 * @since BA-Plus
 */
@SuppressWarnings("serial")
public class OrderedMap<K, V> extends HashMap<K, V> {

    /**
     * Don't waste memory and default to most 4 keys.
     */
    static final int                  DEFAULT_CAPACITY = 4;

    private List<K>                   keys;
    private transient volatile Set<K> kSet             = null;

    /**
	 * 
	 */
    public OrderedMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * @param capacity
     */
    public OrderedMap(int capacity) {
        super(capacity);
        keys = new ArrayList<K>(capacity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        if (oldValue == null) {
            keys.add(key);
        }

        return oldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    public void putAll(Map<? extends K, ? extends V> t) {
        for (Iterator<? extends K> it = t.keySet().iterator(); it.hasNext();) {
            K key = it.next();
            put(key, t.get(key));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.HashMap#keySet()
     */
    public Set<K> keySet() {
        if (kSet == null) {
            kSet = new AbstractSet<K>() {

                @Override
                public Iterator<K> iterator() {
                    return keys.iterator();
                }

                @Override
                public int size() {
                    return keys.size();
                }
            };
        }

        return kSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractMap#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        Iterator<K> it = keySet().iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            K key = it.next();
            V value = get(key);
            sb.append(key == this ? "{this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "{this Map)" : value);

            if (hasNext = it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append('}');
        return sb.toString();
    }

}