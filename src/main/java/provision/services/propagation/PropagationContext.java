package provision.services.propagation;

import java.util.Iterator;
import java.util.Map;


/**
 * Derive and propagate logging context through a system,
 * without having to place context explicitly through the source code.
 * 
 * Modes of propagation {@link ContextPropagationMode} depends currently on what is supported by weblogic
 * context propagation, as the main feature rich implementation is based on it.
 * {@link WLPropagationContextImpl} and a synchronized version {@link WLSyncPropagationCtxImpl}.
 * There is a minimal defailt one available but only constrainted to single thread execution
 * of an application transaction.
 * 
 * 
 * Typically clients will initialize by calling {@link #init(String, String)}
 * or {@link #init(String, String, ContextPropagationMode)}. The {@link ContextPropagationMode}
 * determines the boundary or rather which paths will be chosen to propagate through. 
 * 
 * The request id, will form the basis of the propagation mode, once it is set.
 * The way to choose the propagation mode will depend on if the client wants to
 * make the propagation visible to the headers/messaging that contain the context 
 * information.
 * 
 * 
 * 
 * @author msimonsen
 *
 */

public interface PropagationContext {

	public static final String PREFIX = "LC_";
	public static final String REQUEST_ID_KEY = PREFIX + "REQUEST_ID";
	public static final String NODE_ID_KEY = PREFIX + "NODE_ID";
	public static final String USER_ID_KEY = PREFIX + "USER_ID";
	public static final String SAP_SOLD_TO_KEY = PREFIX + "SAP_SOLD_TO";
	public static final String IP_KEY = PREFIX + "CLIENT_IP";
	
	
	public static final String INTERNAL_FLAG = "INTERNAL";
	/**
	 * The value returned to clients when the key hasn't been set (or set to null)
	 */
	public static final String EMPTY_VALUE = "";
	/**
	 * Initializes the context with a minimum of request id {@link #getId()}.
	 * 
	 */
	public void init();

	/**
	 * Same as {@link #init()} with different context boundary conditions as
	 * the default (which attempts global propagation)
	 * 
	 * Once mode is set, it will remain in this mode for the completion of the call.
	 * 
	 * @param mode
	 */
	public void init(ContextPropagationMode mode);

	/**
	 * Initializes the context with a given userId and sapSoldTo.
	 * It checks to see if there is a request id in the context first, before
	 * assigning one, not to destroy any context that may have appeared earlier on,
	 * by way of filter for example {@link LogContextFilter}.
	 */
	public void init(String userId, String sapSoldTo);

	/**
	 * same as {@link #init(String, String)}.
	 * 
	 * Once mode is set, it will remain in this mode for the completion of the call.
	 * Subsequent calls to init will not change the mode, until an unset {@link #unSetContext()}
	 * is called.
	 * 
	 * @param userId
	 * @param sapSoldTo
	 * @param mode
	 */
	public void init(String userId, String sapSoldTo, ContextPropagationMode mode);
	
	
	/**
	 * Force the initialization of all context values when force attribute is true.
	 * 
	 * @param force
	 * @param mode
	 */
	public void init(boolean force,ContextPropagationMode mode);

	/**
	 * Same as above method that will set the userId, sapSoldTo to "INTERNAL" value.
	 */
	public void forceInternalInit();
	/**
	 * If initialized.
	 * 
	 * @return
	 */
	public boolean isInitialized();

	/**
	 * Unsets all information that was attached to the callers context.
	 * 
	 * 
	 */
	public void unSetContext();

	/**
	 * 
	 * @return uniquely generated id, otherwise null if it hasn't been set
	 */
	public String getId();

	/**
	 * Generally clients do not set this, use {@link #init(String, String)}
	 * or {@link #init(String, String, ContextPropagationMode)}.
	 * 
	 * @param id
	 */
	public void setId(String id);

	public String getUserId();

	public void setUserId(String userId);

	/**
	 * If the node Id has been propagated then return that one, otherwise
	 * return the local one.
	 * 
	 * @return
	 */
	public String getNodeId();

	/**
	 * Sets node id for propagation.
	 * 
	 * @param nodeId
	 */
	public void setNodeId(String nodeId);

	public String getSapSoldTo();

	public void setSapSoldTo(String sapSoldTo);
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getStringCtx(String key);
	
	/**
	 * Propagate a key, value pair throughout a request life-cycle
	 * Will have the default properties defined by {@link ContextPropagationMode}
	 * 
	 * It will over-ride any values that have been previously set, so take note
	 * of the keys defined: {@link #REQUEST_ID_KEY}, {@link #NODE_ID_KEY}, {@link #SAP_SOLD_TO_KEY},
	 * and {@link #USER_ID_KEY}
	 * 
	 * If the value of null is supplied the value will be set to empty value {@link #EMPTY_VALUE}
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	public void setStringCtx(String key,String value);
		
	/**
	 * Get the value associated with above defined key.
	 * 
	 * @param key - the key to store. This will over-ride and value that has been previously set
	 * @param value - the string value to store.
	 * @param mode - {@link ContextPropagationMode}
	 */
	public void setStringCtx(String key,String value,ContextPropagationMode mode);
	
	/**
	 * Returns a set an {@link Iterator<String>} of the keys in the current calling context.
	 * 
	 * @return {@link Iterator<String>}
	 */
	public Iterator<String> keys();
	
	/**
	 * Constructs a Map {@link Map<String,String>} with current information held in {@link PropagationContext}. 
	 * 
	 * @return
	 */
	public Map<String,String> getContextMap();
	
	public void setContextMap(Map<String,String> map);
}