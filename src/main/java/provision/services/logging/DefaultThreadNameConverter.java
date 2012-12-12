/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultThreadNameConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

import provision.util.StringUtil;

/**
 * Converts the custom conversion character {@link PSPatternLayout#THREAD_CONVERSION} in order to
 * add the thread name to the formatted message.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultThreadNameConverter extends PatternConverter {

	/**
	 * Weblogic Thread Name RegEx
	 * [&lt;thread-status&gt;] ExecuteThread: '&lt;thread-number&gt;' for queue: '&lt;queue-name&gt;'
	 */
	private static final String DEFAULT_THREAD_NAME_REGEX = "^(\\[\\w+\\]\\s*)(ExecuteThread:\\s*')\\d+('\\s*for queue:\\s*')[a-zA-Z_0-9\\.]+((\\s*\\(self-tuning\\))?')$";
	private static final int DEFAULT_THREAD_GROUP_NUMBER = 3;
	private static final int DEFAULT_QUEUE_GROUP_NUMBER = 4;
	
	private Pattern thrNamePattern = Pattern.compile(DEFAULT_THREAD_NAME_REGEX);
	private int threadGroupNumber = DEFAULT_THREAD_GROUP_NUMBER;
	private int queueGroupNumber = DEFAULT_QUEUE_GROUP_NUMBER;

	/**
	 * Provides a clean "OFF" switch for this feature.
	 * 
	 * @param thrNameRegEx
	 */
	public void setPattern(String thrNameRegEx) {
		if (StringUtil.isEmpty(thrNameRegEx)) {
			thrNamePattern = null;
		}
		else {
			thrNamePattern = Pattern.compile(thrNameRegEx);
		}
	}
	
	/**
	 * @param threadGroupNumber the threadGroupNumber to set
	 */
	public void setThreadGroupNumber(int n) {
		if (n > 1) {
			this.threadGroupNumber = n;
		}
	}

	/**
	 * @param queueGroupNumber the queueGroupNumber to set
	 */
	public void setQueueGroupNumber(int n) {
		if (n > 1) {
			this.queueGroupNumber = n;
		}
	}

	/**
	 * Threads whose names match the {@link #thrNamePattern} will be displayed as:
	 * <p>
	 * &lt;queue-name&gt[&lt;thread-number&gt;]
	 * Other threads that do not match the pattern, will be displayed as-is.
	 * 
	 * @param event - used to get the local MDC
	 * @return The converted thread name or the one obtained from the local MDC.
	 */
	@Override
	protected String convert(LoggingEvent event) {
		String threadName = (String) event.getMDC(Logger.CALLER_THREAD);
		if (threadName == null) {
			threadName = Thread.currentThread().getName();
		}

		if (thrNamePattern != null) {
			Matcher m = thrNamePattern.matcher(threadName);
			int groupCount = m.groupCount();
		
    		if (m.find() && (threadGroupNumber <= groupCount) && (queueGroupNumber <= groupCount)) {
    			String thrno = threadName.substring(m.end(threadGroupNumber - 1), m.start(threadGroupNumber));
    			String qname = threadName.substring(m.end(queueGroupNumber - 1), m.start(queueGroupNumber));
    			threadName = new StringBuilder(qname).append('[').append(thrno).append(']').toString();
    		}
		}
		
		return threadName;
	}
}