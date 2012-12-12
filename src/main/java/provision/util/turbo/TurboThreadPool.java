/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/TurboThreadPool.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

import java.text.MessageFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import provision.util.StringUtil;
import provision.util.SystemUtil;

/**
 * Custom thread pool. Intercepts when tasks start and finish.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboThreadPool extends ThreadPoolExecutor {

	/**
	 * Create the thread pool with the specified settings.
	 * 
	 * @param coreSize
	 * @param maxSize
	 * @param keepAlive
	 * @param tu
	 * @param queue
	 * @param factory
	 * @param handler
	 */
	public TurboThreadPool(int coreSize, int maxSize, long keepAlive, TimeUnit tu, BlockingQueue<Runnable> queue,
			ThreadFactory factory, RejectedExecutionHandler handler) {
		super(coreSize, maxSize, keepAlive, tu, queue, factory, handler);
	}

	/**
	 * @param r
	 * @param t
	 * @see java.util.concurrent.ThreadPoolExecutor#afterExecute(java.lang.Runnable,
	 *      java.lang.Throwable)
	 */
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);

		if (t != null) {
			StringBuilder sb = new StringBuilder("Task execution has terminated at ").append(SystemUtil.now());
			StringUtil.appendLine(sb, r);

			if (!(getThreadFactory() instanceof Thread.UncaughtExceptionHandler)) {
				StringUtil.appendLine(sb, StringUtil.toString(t));
			}

			System.err.println(sb);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(StringUtil.underline("Thread Pool Details", '-'));
		sb.append(" [active]=").append(this.getActiveCount());
		sb.append(" [completed]=").append(this.getCompletedTaskCount());
		sb.append(" [coreSize]=").append(this.getCorePoolSize());
		sb.append(" [largestSize]=").append(this.getLargestPoolSize());
		sb.append(" [maximumSize]=").append(this.getMaximumPoolSize());
		sb.append(" [currentSize]=").append(this.getPoolSize());
		sb.append(" [taskCount]=").append(this.getTaskCount());

		return sb.toString();
	}

}