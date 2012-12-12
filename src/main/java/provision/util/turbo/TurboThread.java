/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/TurboThread.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

import java.util.concurrent.atomic.*;

/**
 * Custom thread class. Intercepts when threads become alive or are terminated.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboThread extends Thread {

	private static final AtomicInteger aliveCount = new AtomicInteger();

	/**
	 * Creates the thread with the specified group, runnable and name.
	 * 
	 * @param thrGroup
	 * @param r
	 * @param thrName
	 */
	public TurboThread(ThreadGroup thrGroup, Runnable r, String thrName) {
		super(thrGroup, r, thrName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			aliveCount.incrementAndGet();
			super.run();
		}
		finally {
			aliveCount.decrementAndGet();
		}
	}

	/**
	 * Return the number of alive threads belonging to this class.
	 * 
	 * @return
	 */
	public static int getAliveCount() {
		return aliveCount.get();
	}

}
