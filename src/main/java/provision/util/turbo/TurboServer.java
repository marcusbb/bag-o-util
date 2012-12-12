/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/TurboServer.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

/**
 * Generic interface to a multithreaded server.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public interface TurboServer<T> {

	public void setName(String s);

	public void setThreadNamePreffix(String s);

	public void setThreadPoolCoreSize(int n);

	public void setThreadPoolMaxSize(int n);

	public void setThreadKeepAlive(long n);

	public void setTaskQueueCapacity(int n);

	public void setTaskClass(String s);

	public void start();

	public void stop();

	public void doRequest(T taskData);

}
