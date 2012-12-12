/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboAppenderAttachable.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

import provision.util.StringUtil;
import provision.util.SystemUtil;

/**
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboAppenderAttachable implements AppenderAttachable {

	protected List<Appender> appenders = new ArrayList<Appender>(2);
	protected Map<Appender, List<String>> errors;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#addAppender(org.apache.log4j.Appender)
	 */
	public void addAppender(Appender appender) {
		if (appender != null) {
			if (!appenders.contains(appender)) {
				appenders.add(appender);
			}
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public int appendLoopOnAppenders(LoggingEvent event) {
		int size = 0;
		Appender appender = null;

		if (appenders != null) {
			size = appenders.size();
			for (int i = 0; i < size; i++) {
				appender = appenders.get(i);

				try {
					appender.doAppend(event);
				}
				catch (Exception e) {
					// this is not good, it means the error handler does not even know about it
					if (isFirstTime(appender, e)) {
						String errMessage = MessageFormat.format(
								"The appender [{0}] failed to catch an exception at [{1}]", appender.getName(),
								SystemUtil.now());

						StringBuilder sb = new StringBuilder("Log event requested by: ").append(event.getLoggerName());
						Object message = event.getMessage();
						if (TurboLogMessage.isTurboMessage(message)) {
							sb.append(" [id]=").append(TurboLogMessage.getId(message));
							sb.append(" [msg]=").append(TurboLogMessage.getMessage(message));
							sb.append(" [parms]=").append(TurboLogMessage.getParameters(message));
						}
						else {
							sb.append(" [msg]=").append(message);
						}

						ErrorHandler eh = appender.getErrorHandler();
						if (eh != null) {
							System.err.println(sb);
							eh.error(errMessage, e, ErrorCode.WRITE_FAILURE, event);
						}
						else {
							StringUtil.appendLine(sb, errMessage);
							StringUtil.appendLine(sb, StringUtil.toString(e));
							System.err.println(sb);
						}
					}
				}
			}
		}

		return size;
	}

	/**
	 * @param appender
	 * @param e
	 * @return
	 */
	protected boolean isFirstTime(Appender appender, Exception e) {
		List<String> xcps = (errors == null ? null : errors.get(appender));
		if ((xcps != null) && xcps.contains(e.getClass().getName())) {
			return false;
		}

		if (xcps == null) {
			xcps = new ArrayList<String>();
		}

		xcps.add(e.getClass().getName());

		if (errors == null) {
			errors = new HashMap<Appender, List<String>>();
			errors.put(appender, xcps);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#getAllAppenders()
	 */
	public Enumeration<Appender> getAllAppenders() {
		return Collections.enumeration(appenders);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#getAppender(java.lang.String)
	 */
	public Appender getAppender(String name) {
		if (name != null) {
			for (Appender appender : appenders) {
				if (name.equals(appender.getName())) {
					return appender;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#isAttached(org.apache.log4j.Appender)
	 */
	public boolean isAttached(Appender appender) {
		return (appender == null ? false : appenders.contains(appender));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAllAppenders()
	 */
	public void removeAllAppenders() {
		for (Appender appender : appenders) {
			appender.close();
		}

		appenders.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAppender(org.apache.log4j.Appender)
	 */
	public void removeAppender(Appender appender) {
		if (appender != null) {
			appenders.remove(appender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.AppenderAttachable#removeAppender(java.lang.String)
	 */
	public void removeAppender(String name) {
		Appender appender = getAppender(name);
		if (appender != null) {
			appenders.remove(appender);
		}
	}
}
