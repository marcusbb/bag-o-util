/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/TurboThreadFactory.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import provision.util.StringUtil;

/**
 * Custom thread factory. Allows setting user-defined names for threadgroups and threads and control
 * thread creation.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboThreadFactory implements ThreadFactory, Thread.UncaughtExceptionHandler {

	public static final String DEFAULT_NAME_PREFIX = "TurboThread";

	private AtomicInteger createdCount = new AtomicInteger();
	private ThreadGroup thrGroup;
	private String thrNamePrefix = DEFAULT_NAME_PREFIX;
	private boolean createDaemons = true;

	/**
	 * Creates the factory that will produce thread objects as part of the named thread group/pool.
	 * 
	 * @param poolName
	 */
	public TurboThreadFactory(String poolName, String thrPrefix) {
		this.thrGroup = new ThreadGroup(poolName);
		setThreadNamePrefix(thrPrefix);
	}

	/**
	 * @param s
	 */
	public void setThreadNamePrefix(String s) {
		this.thrNamePrefix = s;
	}

	/**
	 * @param b
	 */
	public void setCreateDaemons(boolean b) {
		this.createDaemons = b;
	}

	/**
	 * Customize the thread name and exception handler.
	 * 
	 * @param r
	 * @return
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	public Thread newThread(Runnable r) {
		Thread t = new TurboThread(this.thrGroup, r, getThreadName());
		t.setUncaughtExceptionHandler(this);

		if (createDaemons) t.setDaemon(true);
		return t;
	}

	/**
	 * @return
	 */
	protected String getThreadName() {
		int count = createdCount.incrementAndGet();
		String thName = thrNamePrefix + count;
		return thName;
	}

	/**
	 * @param t
	 * @param e
	 * @see java.lang.Thread$UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	public void uncaughtException(Thread t, Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("Thread [{0}] terminated due to an uncaught exception:", t.getName()));
		StringUtil.appendLine(sb, StringUtil.toString(e));
		System.err.println(sb);
	}

	/**
	 * Return the number of threads created by this factory.
	 * 
	 * @return
	 */
	public int getCreatedCount() {
		return createdCount.get();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(StringUtil.underline("Thread Factory Details", '-'));
		sb.append(" [threadGroup]=").append(this.thrGroup);
		sb.append(" [threadPrefix]=").append(this.thrNamePrefix);
		sb.append(" [createdCount]=").append(this.getCreatedCount());

		return sb.toString();
	}

}