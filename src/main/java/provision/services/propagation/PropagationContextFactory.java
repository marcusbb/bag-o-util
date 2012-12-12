/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id$
 * $Author$ $DateTime$ $Change$
 */
package provision.services.propagation;

import java.util.Properties;

import provision.services.management.NodeIdUtil;
import provision.util.ResourceUtil;

/**
 * The entry point to retrieve a handle to the {@link PropagationContext}.
 * 
 * @author msimonsen
 *
 */
public class PropagationContextFactory {

	private static String DEFAULT_PROPOGATION_CONTEXT_CONFIG_FILE = "propagation_config.properties";
	private static String LOG_PROPAGATION_PROP = "LOG_PROPAGATION";
	
	private static PropagationContext ctx = null;
		
	static {
	
		init();
		
	}
	
	public static PropagationContext get() {
		
		return ctx;
	}
	
	protected static PropagationContext create() {
//        if (BaseUtil.isStandalone()) {
//            return null;
//        }

	    PropagationContext ctx = null;
		System.out.println("################LOG_CONTEXT_PROPAGATION################");
		System.out.println("LP[cl]: " + Thread.currentThread().getContextClassLoader());
		System.out.println("LP[tname]: " + Thread.currentThread().getName());
		System.out.println("LP[nodeId]: " + NodeIdUtil.getNodeId());
		
		try {
			
			Properties properties = ResourceUtil.getResourceAsProperties(DEFAULT_PROPOGATION_CONTEXT_CONFIG_FILE);
			if(properties != null && properties.containsKey(LOG_PROPAGATION_PROP)){
				String cName = properties.getProperty(LOG_PROPAGATION_PROP);
				if (cName != null) {
					ctx = (PropagationContext)Class.forName(cName).newInstance();
					System.out.println("LP[fileImpl]: " + ctx.getClass().getName());
				}
			}
			
		} catch (Exception  e) {
			//Commenting out logging for now. Reported strange Runtime initialization
			//exception in Logger class
			//Logger.warn(WLPropagationContextImpl.class.getName(), "create", null,e.getMessage(),e);
			e.printStackTrace();
		}
		if (ctx == null) {
			ctx = new PropagationCtxEmptyImpl();
			System.out.println("LP[emptyImpl]: " + ctx.getClass().getName());
		}
		System.out.println("LP[objInst]: " + ctx.toString());
		System.out.println("################LOG_CONTEXT_PROPAGATION################");
		
		return ctx;
	}
	
	protected static void init() {
		ctx = create();
		//Commenting out logging for now. Reported strange Runtime initialization
		//exception in Logger class
		//Logger.infox("PropagationContext: " + ctx.getClass().getSimpleName());
		
	}
	
}
