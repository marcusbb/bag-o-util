/*
 * (C) 2008 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/JMXUtil.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import provision.services.logging.LogConfigurator;
import provision.services.logging.Logger;
import provision.services.logging.PSTokenDictionary;

/**
 * @author Iulian Vlasov
 */
public class JMXUtil {
	private static final String CALLER = "JMXUtil";
	
	private static final String JMX_SERVER_CTX = "java:comp/jmx/runtime";		
	public static final String SERVER_RUNTIME_MBEAN_TYPE = "ServerRuntime";
		
	/**
	 * Call this method from within JEE container to obtain a reference to the
	 * MBeanServer.
	 * 
	 * @return
	 * @throws NamingException
	 */
	public static MBeanServer getServer() throws NamingException {
		InitialContext ctx = null;
		try {
			ctx = new InitialContext();
			return (MBeanServer) ctx.lookup(JMX_SERVER_CTX);
		}
		finally {
			try {
				if (ctx != null)
					ctx.close();
			}
			catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param server
	 * @param agentName
	 * @param mbeanType
	 * @param mbeanName
	 * @param mbeanObject
	 * @return The ObjectName of the MBean if successfully registered.
	 * 
	 * @throws JMException
	 */
	public static ObjectName registerMBean(MBeanServer server, String agentName, Map<String,String> attrs, Object mbeanObject) throws JMException {
		StringBuilder sb = new StringBuilder(agentName).append(':');
		for (String key : attrs.keySet()) {
			sb.append(key).append('=').append(attrs.get(key)).append(',');
		}
		
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		
		ObjectName oName = new ObjectName(sb.toString());
		if (server.isRegistered(oName)) {
			try {
				server.unregisterMBean(oName);
			}
			catch (Throwable ignored) {}
		}

		server.registerMBean(mbeanObject, oName);
		return oName;
	}
	
	/**
	 * @param server
	 * @param oNames
	 * @return A list of ObjectNames that failed to be unregistered.
	 */
	public static List<ObjectName> unregisterMBeans(MBeanServer server, ObjectName[] oNames) {
		List<ObjectName> failed = new ArrayList<ObjectName>(oNames.length);
		
		for (ObjectName oName : oNames) {
			if (server.isRegistered(oName)) {
				try {
					server.unregisterMBean(oName);
				}
				catch (Throwable t) {
					t.printStackTrace();
					failed.add(oName);
				}
			}
		}
		
		return failed;
	}
	
    public static void unregisterMBeans(ObjectName[] managedBeans, String srvcName) {
        if (!BaseUtil.isEmpty(managedBeans)) {
            try {
                MBeanServer server = getServer();
                List<ObjectName> failed = unregisterMBeans(server, managedBeans);
                for (ObjectName oName : managedBeans) {
                    if (failed.contains(oName)) {
                        Logger.warn(CALLER, "Failed to unregister MBean: " + oName);
                    }
                    else {
                        Logger.warn(CALLER, "Successfully unregistered MBean: " + oName);
                    }
                }
            }
            catch (NamingException e) {
                Logger.error(CALLER, "Unable to unregister service MBeans: " + srvcName, e);
            }
        }
    }
	
	/**
	 * @param agentName
	 * @param mbeanType
	 * @param mbeanObject
	 * @return
	 */
	public static ObjectName[] registerMBeans(String agentName, String mbeanType, Object mbeanObject) {
		if (StringUtil.getStringProperty("weblogic.Name", null) == null) {
			return new ObjectName[] {};
		}

		MBeanServer server = null;
		try {
			server = JMXUtil.getServer();
		}
		catch (Throwable t) {
			// don't let anything spill, since this is an optional feature
			Logger.warn(CALLER, t, "Failed to retrieve an instance of the mbean server");
			return new ObjectName[] {};
		}

		String mbeanName = SystemUtil.getContextName();

		Map<String, String> attrs = new PSTokenDictionary<String, String>();
		attrs.put("type", mbeanType);
		attrs.put("name", mbeanName);

		ObjectName beanName = null;
		try {
			beanName = registerMBean(server, agentName, attrs, mbeanObject);
			Logger.infox("Successfully registered MBean: " + beanName);
		}
		catch (Throwable t) {
			if (t instanceof InstanceAlreadyExistsException) {
				Logger.warn(CALLER, t, "MBean already registered with the server: " + attrs);
			}
			else {
				Logger.warn(CALLER, t, "Failed to register MBean: " + attrs);
				return new ObjectName[] {};
			}
		}

		return new ObjectName[] { beanName };
	}
	
	public static String getContextName(Object o) {
		if (o == null) {
			o = LogConfigurator.class;
		}

		Class<?> c = (o instanceof Class ? (Class<?>) o : o.getClass());
		ClassLoader cl = c.getClassLoader();
		String clname = cl.toString();
		String token = "annotation: ";
		int ix = clname.lastIndexOf(token);
		if (ix != -1) {
			clname = clname.substring(ix + token.length());
			if (clname.endsWith("@")) {
				clname = clname.substring(0, clname.length() - 1);
			}
		}

		return clname;
	}
	
	/**
	 * Retrieve an instance of the {@link java.lang.reflect.Method} for an MBean operation.
	 * 
	 * @param mbeanInterface
	 * @param mbeanOperation
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	public static Method getMethodForOperation(Class<?> mbeanInterface, MBeanOperationInfo mbeanOperation) throws SecurityException, NoSuchMethodException, ClassNotFoundException{
		if((mbeanInterface == null) || (mbeanOperation == null)){
			throw new IllegalArgumentException("Call to getMethodForOperation did not specify mbeanInterface or mbeanOperation");
		}
		
		Class<?>[] paramClasses = getMethodSignatureClass(mbeanInterface, mbeanOperation.getName(), mbeanOperation.getSignature());
		
		
		//now look up the Method based on the signature
		return mbeanInterface.getMethod(mbeanOperation.getName(), paramClasses);
	}
	
	/**
	 * Retrieves the set of Class {@link java.lang.Class<?>} that represents the type of of each parameter in an
	 * MBean operation's signature. This is necessary because MBeanParameterInfo does not contains parameter type
	 * as instances of classes but instead it stores them as String.
	 * 
	 * @param mbeanInterface
	 * @param name
	 * @param paramsInfo
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?>[] getMethodSignatureClass(Class<?> mbeanInterface, String name, MBeanParameterInfo[] paramsInfo) throws ClassNotFoundException{		
		if((mbeanInterface == null) || (name == null) || ("".equals(name))){
			throw new IllegalArgumentException("Call to getMethodSignatureClass did not specify mbeanInterface or name");
		}
		
		if((paramsInfo == null) || (paramsInfo.length == 0)){
			return null;
		}
		
		ClassLoader classLoader = mbeanInterface.getClassLoader();
		Class<?> []paramClasses = new Class<?>[paramsInfo.length];
		for(int i = 0; i < paramsInfo.length; i++){
			MBeanParameterInfo curParamInfo = paramsInfo[i];
			paramClasses[i] = ReflectionUtil.classForName(curParamInfo.getType(), classLoader);
		}	
		
		return paramClasses;
	}
}
