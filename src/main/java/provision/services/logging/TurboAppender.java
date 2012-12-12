/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboAppender.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.LoggingEvent;

import provision.util.turbo.AbstractTurboTask;
import provision.util.turbo.DefaultTurboServer;
import provision.util.turbo.TurboTask;

/**
 * Custom asynchronous appender running a thread pool for serving logging requests.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboAppender extends AppenderSkeleton implements AppenderAttachable {

	public final static int DEFAULT_BUFFER_SIZE = 8192;
	public final static int DEFAULT_KEEP_ALIVE = 10;
	public final static int DEFAULT_POOL_CORE_SIZE = 1;
	public final static int DEFAULT_POOL_MAX_SIZE = 1;
	public final static String DEFAULT_THREAD_ID = "TURBO";

	protected int bufferSize = DEFAULT_BUFFER_SIZE;
	protected boolean locationInfo;
	protected long threadKeepAlive = DEFAULT_KEEP_ALIVE;
	protected int threadPoolCoreSize = DEFAULT_POOL_CORE_SIZE;
	protected int threadPoolMaxSize = DEFAULT_POOL_MAX_SIZE;
	protected String threadIdentifier = DEFAULT_THREAD_ID;
	
	protected TurboAppenderAttachable appenders;
	protected TurboLoggingServer server;

	/**
	 * 
	 */
	public TurboAppender() {
		this.appenders = new TurboAppenderAttachable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#addAppender(org.apache.log4j.Appender)
	 */
	public void addAppender(Appender newAppender) {
		synchronized (appenders) {
			appenders.addAppender(newAppender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	public void append(LoggingEvent event) {
		event.getNDC();
		event.getThreadName();
		event.getMDCCopy();

		if (locationInfo) {
			event.getLocationInformation();
		}

		server.doRequest(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	public void close() {
		synchronized (this) {
			if (closed) {
				return;
			}
			closed = true;
		}

		server.stop();
		appenders.removeAllAppenders();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#activateOptions()
	 */
	public void activateOptions() {
		this.server = createTurboServer();
		server.start();
	}

	/**
	 * The <code>AsyncAppender</code> does not require a layout. Hence, this method always returns
	 * <code>false</code>.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#getAllAppenders()
	 */
	public Enumeration<Appender> getAllAppenders() {
		synchronized (appenders) {
			return appenders.getAllAppenders();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#getAppender(java.lang.String)
	 */
	public Appender getAppender(String name) {
		synchronized (appenders) {
			return appenders.getAppender(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAllAppenders()
	 */
	public void removeAllAppenders() {
		synchronized (appenders) {
			appenders.removeAllAppenders();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAppender(org.apache.log4j.Appender)
	 */
	public void removeAppender(Appender appender) {
		synchronized (appenders) {
			appenders.removeAppender(appender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAppender(java.lang.String)
	 */
	public void removeAppender(String name) {
		synchronized (appenders) {
			appenders.removeAppender(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#isAttached(org.apache.log4j.Appender)
	 */
	public boolean isAttached(Appender appender) {
		synchronized (appenders) {
			return appenders.isAttached(appender);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (server != null) {
			int capacity = server.getQueueCapacity();
			int size = server.getQueueSize();

			sb.append("[Total Messages]=").append(server.getCompletedTaskCount());
			sb.append(" [Queue Capacity]=").append(capacity);
			sb.append(" [Current Size]=").append(size);
			sb.append(" [Remaining]=").append(capacity - size);
		}

		return sb.toString();
	}

	/**
	 * @param value the queueCapacity to set
	 */
	public void setBufferSize(int value) {
		this.bufferSize = value;
	}

	/**
	 * @param flag
	 */
	public void setLocationInfo(boolean flag) {
		locationInfo = flag;
	}

	/**
	 * @param n the threadKeepAlive to set
	 */
	public void setThreadKeepAlive(long n) {
		this.threadKeepAlive = n;
	}

	/**
	 * @param n the threadPoolCoreSize to set
	 */
	public void setThreadPoolCoreSize(int n) {
		this.threadPoolCoreSize = n;
	}

	/**
	 * @param n the threadPoolMaxSize to set
	 */
	public void setThreadPoolMaxSize(int n) {
		this.threadPoolMaxSize = n;
	}

	/**
	 * @param s the threadIdentifier to set
	 */
	public void setThreadIdentifier(String s) {
		this.threadIdentifier = s;
	}

	/**
	 * Gets the capacity of the internal queue.
	 * 
	 * @return the current value of the <b>BufferSize</b> option.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/**
	 * Returns the current value of the <b>LocationInfo</b> option.
	 * 
	 * @return
	 */
	public boolean getLocationInfo() {
		return locationInfo;
	}

	/**
	 * @return
	 */
	public TurboLoggingServer getServer() {
		return server;
	}

	/**
	 * @return
	 */
	protected TurboLoggingServer createTurboServer() {
		return new TurboLoggingServer();
	}

	/**
	 * @author Iulian Vlasov
	 */
	class TurboLoggingServer extends DefaultTurboServer<LoggingEvent> {

		TurboLoggingServer() {
			super("Turbo Logging");
			setTaskQueueCapacity(bufferSize);
			setThreadKeepAlive(threadKeepAlive);
			setThreadPoolCoreSize(threadPoolCoreSize);
			setThreadPoolMaxSize(threadPoolMaxSize);

			StringBuilder sb = new StringBuilder(threadIdentifier);
			sb.append('-').append(LogConfigurator.getContextName());
			setThreadNamePreffix(sb.toString());
		}

		protected TurboTask<LoggingEvent> createTask(LoggingEvent taskData) {
			return new TurboLoggingTask(taskData);
		}
	}

	/**
	 * @author Iulian Vlasov
	 */
	class TurboLoggingTask extends AbstractTurboTask<LoggingEvent> {

		public TurboLoggingTask(LoggingEvent data) {
			super(data);
		}

		public void doTask(LoggingEvent taskData) {
			appenders.appendLoopOnAppenders(taskData);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("Log event requested by: ").append(taskData.getLoggerName());
			Object message = taskData.getMessage();
			if (TurboLogMessage.isTurboMessage(message)) {
				sb.append(" [id]=").append(TurboLogMessage.getId(message));
				sb.append(" [msg]=").append(TurboLogMessage.getMessage(message));
				sb.append(" [parms]=").append(TurboLogMessage.getParameters(message));
			}
			else {
				sb.append(" [msg]=").append(message);
			}
			
			return sb.toString();
		}
	}

}
