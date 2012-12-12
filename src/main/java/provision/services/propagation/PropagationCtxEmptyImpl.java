package provision.services.propagation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * THIS IS A NON-IMPLEMENTS CLASS.
 * 
 * TO BE REMOVED FOR NEXT PRODUCTION RELEASE.
 * {@link WLPropagationContextImpl#getInstance()} must be modified.
 * 
 * @author msimonsen
 *
 */
public class PropagationCtxEmptyImpl implements PropagationContext {

	
	private static String EMPTY_STR = PropagationContext.EMPTY_VALUE;
	private static Map<String,String> EMPTY_MAP = new HashMap<String,String>();
	
	protected PropagationCtxEmptyImpl() {
		
	}
			

	
	public String getId() {
		return EMPTY_STR;
	}

	
	public String getNodeId() {
		return EMPTY_STR;
	}

	
	public String getSapSoldTo() {
		return EMPTY_STR;
	}

	
	public String getUserId() {
		return EMPTY_STR;
	}

	
	public void init() {
		
	}

	
	public void init(ContextPropagationMode mode) {
		
	}

	
	public void init(String userId, String sapSoldTo, ContextPropagationMode mode) {
		
	}

	
	public void init(String userId, String sapSoldTo) {
		
	}

		
	public void forceInternalInit() {
		
		
	}



	public void init(boolean force, ContextPropagationMode mode) {
		
		
	}



	public boolean isInitialized() {
		return true;
	}

	
	public void setId(String id) {
		
	}

	
	public void setNodeId(String nodeId) {
		
	}

	
	public void setSapSoldTo(String sapSoldTo) {
		
	}

	
	public void setUserId(String userId) {
		
	}

	
	public void unSetContext() {
		
	}


	
	public String getStringCtx(String key) {
		return EMPTY_STR;
	}


	
	public void setStringCtx(String key, String value, ContextPropagationMode mode) {
		
	}


	
	public void setStringCtx(String key, String value) {
		
	}


	public Map<String, String> getContextMap() {
		return EMPTY_MAP;
	}

	
	
	public void setContextMap(Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}



	public Iterator<String> keys() {
		return new Iterator<String>() {

			public boolean hasNext() {
				
				return false;
			}

			public String next() {
				
				return null;
			}

			public void remove() {
				
			}
		};
	}
	
	
}
