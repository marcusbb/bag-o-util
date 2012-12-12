/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/DefaultTurboServer.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

import java.security.AccessControlException;
import java.security.Permission;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import provision.util.ReflectionUtil;
import provision.util.StringUtil;

/**
 * @author Iulian Vlasov
 * @since TurboPrv
 * @param <T>
 */
public class DefaultTurboServer<T> implements TurboServer<T> {

	static final String DEFAULT_SERVER_NAME = "Default Turbo Server";
	static final String DEFAULT_THREAD_NAME_PREFIX = "TurboWorker";
	static final int DEFAULT_CORE_POOL_SIZE = 10;
	static final int DEFAULT_MAX_POOL_SIZE = 15;
	static final int DEFAULT_KEEP_ALIVE = 10;
	static final int DEFAULT_QUEUE_SIZE = 200;

	private static final Permission shutdownPermission = new RuntimePermission("modifyThread");

	protected String name = DEFAULT_SERVER_NAME;
	protected int corePoolSize = DEFAULT_CORE_POOL_SIZE;
	protected int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
	protected long keepAlive = DEFAULT_KEEP_ALIVE;
	protected int queueCapacity = DEFAULT_QUEUE_SIZE;
	protected String threadNamePreffix = DEFAULT_THREAD_NAME_PREFIX;
	protected ThreadPoolExecutor pool;
	protected Class<TurboTask<T>> taskClass;

	/**
	 * 
	 */
	public DefaultTurboServer() {
		this(DEFAULT_SERVER_NAME);
	}

	/**
	 * @param s
	 */
	public DefaultTurboServer(String s) {
		this.name = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#setName(java.lang.String)
	 */
	public void setName(String s) {
		this.name = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see provision.util.turbo.TurboServer#setThreadNamePreffix(java.lang.String)
	 */
	public void setThreadNamePreffix(String threadNamePreffix) {
		this.threadNamePreffix = threadNamePreffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#setThreadPoolCoreSize(int)
	 */
	public void setThreadPoolCoreSize(int n) {
		this.corePoolSize = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#setThreadPoolMaxSize(int)
	 */
	public void setThreadPoolMaxSize(int n) {
		this.maxPoolSize = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#setThreadKeepAlive(long)
	 */
	public void setThreadKeepAlive(long n) {
		this.keepAlive = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#setTaskQueueCapacity(int)
	 */
	public void setTaskQueueCapacity(int n) {
		this.queueCapacity = n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see provision.util.turbo.TurboServer#setTaskClass(java.lang.String)
	 */
	public void setTaskClass(String value) {
		try {
			this.taskClass = ReflectionUtil.newClass(value, TurboTask.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#start()
	 */
	public void start() {
		this.pool = createThreadPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#stop()
	 */
	public void stop() {
		boolean changed = false;

		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			try {
				java.security.AccessController.checkPermission(shutdownPermission);
			}
			catch (AccessControlException ace) {
				try {
					System.setSecurityManager(null);
					changed = true;
				}
				catch (SecurityException ignored) {
					// if we reach here, shutdown will fail
				}
			}
		}

		try {
			pool.shutdown();
		}
		catch (AccessControlException ace) {
			System.err.println("Security manager prevents graceful shutdown of the thread pool");
		}

		if (changed) {
			try {
				System.setSecurityManager(sm);
			}
			catch (SecurityException se) {
				System.err.println("Failed to replace the security manager.");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipoc.concurrent.TurboServer#doRequest(java.lang.Object)
	 */
	public void doRequest(T taskData) {
		if (isStopRequest(taskData)) {
			pool.execute(new Runnable() {
				public void run() {
					stop();
				}
			});
		}
		else {
			TurboTask<T> task = createTask(taskData);
			if (task != null) {
				pool.execute(task);
			}
		}
	}

	public long getCompletedTaskCount() {
		return pool.getCompletedTaskCount();
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public int getQueueSize() {
		return pool.getQueue().size();
	}

	/**
	 * String representation of this server.
	 * 
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(StringUtil.underline("Server Name: " + name, '-'));
		sb.append(pool);

		if (pool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor tpe = (ThreadPoolExecutor) pool;
			StringUtil.appendLine(sb, tpe.getThreadFactory());

			StringUtil.appendLine(sb, StringUtil.underline("WORK QUEUE", '-'));
			sb.append(" [Queue Size]=").append(tpe.getQueue().size());
			sb.append(" [Remaining Capacity]=").append(tpe.getQueue().remainingCapacity());
		}

		return sb.toString();
	}

	// **************************
	// PROTECTED IMPLEMENTATION *
	// **************************
	/**
	 * Allow subclasses to create a different type of thread pool.
	 * 
	 * @return
	 */
	protected ThreadPoolExecutor createThreadPool() {
		BlockingQueue<Runnable> queue = createWorkQueue();
		ThreadFactory factory = createThreadFactory();
		RejectedExecutionHandler handler = createRejectedHandler();

		ThreadPoolExecutor tpe = new TurboThreadPool(corePoolSize, maxPoolSize, keepAlive, TimeUnit.SECONDS, queue,
				factory, handler);
		tpe.prestartCoreThread();

		return tpe;
	}

	/**
	 * Allow subclasses to create a different type of work queue.
	 * 
	 * @return
	 */
	protected BlockingQueue<Runnable> createWorkQueue() {
		return new ArrayBlockingQueue<Runnable>(queueCapacity);
	}

	/**
	 * Allow subclasses to use a different type of thread factory.
	 * 
	 * @return
	 */
	protected ThreadFactory createThreadFactory() {
		return new TurboThreadFactory(name, threadNamePreffix);
	}

	/**
	 * Allow subclasses to define a different rejection policy.
	 * 
	 * @return
	 */
	protected RejectedExecutionHandler createRejectedHandler() {
		return new ThreadPoolExecutor.CallerRunsPolicy();
	}

	/**
	 * @param taskData
	 * @return
	 */
	protected TurboTask<T> createTask(T taskData) {
		TurboTask<T> task = null;
		if (taskClass != null) {
			try {
				task = (TurboTask<T>) ReflectionUtil.newObject(taskClass, taskData);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return task;
	}

	/**
	 * @param taskData
	 * @return
	 */
	protected boolean isStopRequest(Object taskData) {
		return (taskData == null);
	}

}
