package provision.services.propagation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import provision.services.management.NodeIdUtil;

/**
 * A default implementation of {@link PropagationContext} that has no dependencies on weblogic
 * libraries.
 * Only propagates through the life-time of a single thread.
 * 
 * Modifications can be made to JMS headers to propagate information over the asynchronous messaging
 * system.
 * 
 * Other thread pooling mechanisms must decide to propagate information based on a call to
 * {@link #getContextMap()} and then either marking it to the eventual consumption of that work
 * 
 * @author msimonsen
 *
 */
public class PropagationContextDefaultImpl implements PropagationContext {

	private static String localNodeId = "UNKNOWN_HOST";
	private IDGenerator idGenerator = null;
	private static int DEF_CAPACITY = 4;
	protected PropagationContextDefaultImpl() {
		idGenerator = new UIDTypeIDImpl();
	}
	static {
		
		localNodeId = NodeIdUtil.getNodeId();
		
	}
	
	private static class ThreadLocalMap extends ThreadLocal<Map<String,String>> {

		@Override
		protected Map<String, String> initialValue() {
			//A bucket of 4 
			HashMap<String,String> map = new HashMap<String, String>(DEF_CAPACITY);
			return map;
		}
		
		
	}
	private static final ThreadLocalMap tLocalMap = new ThreadLocalMap();
	
	
	
	
	//
	public Map<String, String> getContextMap() {
		return tLocalMap.get();
	}

	public String getId() {
		return getStringCtx(PropagationContext.REQUEST_ID_KEY);
	}

	public String getNodeId() {
		return getStringCtx(PropagationContext.NODE_ID_KEY);
	}

	public String getSapSoldTo() {
		return getStringCtx(PropagationContext.SAP_SOLD_TO_KEY);
	}

	

	public String getUserId() {
		return getStringCtx(PropagationContext.USER_ID_KEY);
	}
	
	
	
	public void forceInternalInit() {
		init(true,ContextPropagationMode.ALL);
		setSapSoldTo(PropagationContext.INTERNAL_FLAG);
		setUserId(PropagationContext.INTERNAL_FLAG);
		
	}

	public void init(boolean force, ContextPropagationMode mode) {
		if (force) {
			HashMap<String,String> map =	new HashMap<String, String>();
			tLocalMap.set(map);
			map.put(PropagationContext.REQUEST_ID_KEY, idGenerator.generateID());
		}else {
			init(mode);
		}
		
	}

	public void init() {
		init(null,null);

	}

	public void init(ContextPropagationMode mode) {
		init(null,null,mode);

	}

	public void init(String userId, String sapSoldTo) {
		init(userId,sapSoldTo,ContextPropagationMode.ALL);

	}

	public void init(String userId, String sapSoldTo, ContextPropagationMode mode) {
		Map<String,String> map = tLocalMap.get();
		if (!isInitialized())
			map.put(PropagationContext.REQUEST_ID_KEY, idGenerator.generateID());
		map.put(PropagationContext.USER_ID_KEY, userId);
		map.put(PropagationContext.SAP_SOLD_TO_KEY, sapSoldTo);
		map.put(PropagationContext.NODE_ID_KEY,localNodeId);
	}

	public boolean isInitialized() {
		return !getStringCtx(PropagationContext.REQUEST_ID_KEY).equals(PropagationContext.EMPTY_VALUE);
	}

	public Iterator<String> keys() {
		return tLocalMap.get().keySet().iterator();
	}

	public void setId(String id) {
		setStringCtx(PropagationContext.REQUEST_ID_KEY,id);

	}

	public void setNodeId(String nodeId) {
		setStringCtx(PropagationContext.NODE_ID_KEY, nodeId);

	}

	public void setSapSoldTo(String sapSoldTo) {
		setStringCtx(PropagationContext.SAP_SOLD_TO_KEY, sapSoldTo);

	}

	public void setStringCtx(String key, String value) {
		String nVal = PropagationContext.EMPTY_VALUE;
		if (value != null)
			nVal = value;
		if (key != null )
			tLocalMap.get().put(key, nVal);

	}

	public String getStringCtx(String key) {
		String value = tLocalMap.get().get(key);
		
		if (value != null)
			return value;
		return PropagationContext.EMPTY_VALUE;
	}
	
	public void setStringCtx(String key, String value, ContextPropagationMode mode) {
		setStringCtx(key, value);
	}

	public void setUserId(String userId) {
		setStringCtx(userId, PropagationContext.USER_ID_KEY);

	}

	public void unSetContext() {
		tLocalMap.set(new HashMap<String, String>(DEF_CAPACITY));

	}

	@Override
	public void setContextMap(Map<String, String> map) {
		tLocalMap.get().putAll(map);
		
	}

	
}
