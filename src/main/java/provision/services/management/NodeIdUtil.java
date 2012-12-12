package provision.services.management;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

/**
 * Provides a few mechanisms for doing "node id" lookups.
 * 
 * The methods described below will not yield the same results as
 * {@link #getNodeIdFromPlatform()} and {@link #getNodeIdFromNet()} determine
 * the host name of the current machine,
 * whereas {@link #getWLRuntime()} and {@link #getNodeIdFromSystemParam()} will
 * generally get weblogic's alias for this running node. 
 * 
 * @author msimonsen
 *
 */
public class NodeIdUtil {

		
	public static String WL_NODE_ID_PROP = "weblogic.Name";
	public static String WL_JNDI_RUNTIME_MBEAN_SERVER = "java:comp/env/jmx/runtime";
	
	/**
	 * Non synchronized / non thread safe way of performing a node id look-up.
	 * It is weblogic specific behaviour in it logic.
	 * 
	 * @return
	 */
	public  static String getNodeId() {
		String nodeId = null;
		nodeId = getNodeIdFromSystemParam();
		if (nodeId == null)
			nodeId = getWLRuntime();
		if (nodeId == null)
			nodeId = getNodeIdFromPlatform();
		return nodeId;
		
	}
	/**
	 * This is a fairly expensive mechanism to look up the node's name.
	 * First performs a jndi lookup of the server runtime mbean server.
	 * BEA recommends this approach when performing lookup to a local {@link MBeanServer}.
	 * It does a query all on all the mbeans running on the server and makes
	 * a determination whether one has an attribute "serverRuntime" which corresponds
	 * to the nodes alias name.
	 *  
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getWLRuntime() {
		String nodeId = null;
		try {
			
			InitialContext ctx = new InitialContext();
			
			MBeanServer server = (MBeanServer)ctx.lookup(WL_JNDI_RUNTIME_MBEAN_SERVER);
			Set<ObjectName>nameSet = server.queryNames(new ObjectName("com.bea:*"), null);
			for (ObjectName name:nameSet) {
				try {
					nodeId = name.getKeyProperty("ServerRuntime");
				}catch (Throwable t) {
					t.printStackTrace();
				}
				if (nodeId != null)
					break;
			}
		} catch (Exception e) {
			//Logger.info(NodeIdUtil.class.getName(), "getWLRuntime", e.getMessage());
		}
		return nodeId;
	}
	public static String getNodeIdFromPlatform() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName().substring(runtime.getName().indexOf("@")+1);
		return name;
		
	}
	public static String getNodeIdFromNet() {
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			//Logger.info(NodeIdUtil.class.getName(), "getNodeIdFromNet", e.getMessage());
		}
		return hostName;
		
	}
	public static String getNodeIdFromSystemParam() {
		return System.getProperty(WL_NODE_ID_PROP);
	}
}
