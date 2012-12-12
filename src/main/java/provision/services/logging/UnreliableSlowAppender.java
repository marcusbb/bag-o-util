/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/UnreliableSlowAppender.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Use this appender to simulate appender failures and test error handling procedures.
 * 
 * @author Iulian Vlasov
 */
public class UnreliableSlowAppender extends RollingFileAppender {

	private AtomicInteger counter = new AtomicInteger();
	private int maxEvents;
	private long delay;

	/**
	 * Set the maximum number of logging events before the error handler kicks in.
	 * 
	 * @param maxEvents
	 */
	public void setMaxEvents(int maxEvents) {
		this.maxEvents = maxEvents;
	}

	/**
	 * Set the time in milliseconds to delay each logging event.
	 * 
	 * @param delay
	 */
	public void setDelay(long l) {
		this.delay = l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.RollingFileAppender#subAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void subAppend(LoggingEvent event) {
		int i = counter.incrementAndGet();
		if ((maxEvents <=0) || (i <= maxEvents)) {
			if (delay > 0) {
				try {
					Thread.sleep(delay);
				}
				catch (InterruptedException ignored) {}
			}

			super.subAppend(event);
		}
		else {
			errorHandler.error("I've logged enough (" + (i-1) + " events) and now it is time to fail!", new Exception("Enough is enough!"), 0, event);
			counter.set(0);
		}
	}
}
