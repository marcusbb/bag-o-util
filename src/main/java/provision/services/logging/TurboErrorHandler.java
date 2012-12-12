/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboErrorHandler.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

import provision.util.StringUtil;

/**
 * This error handler uses a failover appender to replace the one that fails within the specified
 * list of AppenderAttachable elements.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboErrorHandler implements ErrorHandler {

	protected Appender backup;
	protected Appender primary;
	protected List<AppenderAttachable> attachables;
	protected List<String> attachableNames;
	protected boolean activated;
	protected String prefix = "Turbo-EH:";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.ErrorHandler#error(java.lang.String)
	 */
	public void error(String message) {
		System.err.println(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.ErrorHandler#error(java.lang.String, java.lang.Exception, int)
	 */
	public void error(String message, Exception e, int errorCode) {
		error(message, e, errorCode, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.ErrorHandler#error(java.lang.String, java.lang.Exception, int,
	 * org.apache.log4j.spi.LoggingEvent)
	 */
	public void error(String message, Exception e, int errorCode, LoggingEvent event) {
		if (!activated) {
			activated = activate();
		}

		LogLog.error(MessageFormat.format("{0} An error has occurred: {1}", prefix, message), e);

		if (!StringUtil.isEmpty(attachables)) {
			LogLog.warn(prefix + " Starting the error handling procedure.");

			for (AppenderAttachable appatt : attachables) {
				String parentName = (appatt instanceof Appender ? ((Appender) appatt).getName() : ((Logger) appatt).getName());

				Appender app = appatt.getAppender(primary.getName());
				if (app != null) {
					LogLog.warn(MessageFormat.format("{0} Dettach [{1}] from [{2}]!", prefix, primary.getName(), parentName));
					appatt.removeAppender(primary);
					primary.close();

					LogLog.warn(MessageFormat.format("{0} Attach [{1}] to [{2}]!", prefix, backup.getName(), parentName));
					appatt.addAppender(backup);
				}

				app = appatt.getAppender(backup.getName());
				if (app != null) {
					// the worst that could happen is logging the same event multiple times by the backup appender.
					backup.doAppend(event);
				}
				else {
					// should not happen
					LogLog.warn(MessageFormat.format("{0} Could not find [{1}] attached to [{2}]!", prefix, backup.getName(), parentName));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.ErrorHandler#setAppender(org.apache.log4j.Appender)
	 */
	public void setAppender(Appender appender) {
		this.primary = appender;
		LogLog.debug(MessageFormat.format("{0} Primary appender: [{1}].", prefix, appender.getName()));
	}

	/**
	 * Set the backup appender.
	 */
	public void setBackupAppender(Appender appender) {
		this.backup = appender;
		LogLog.debug(MessageFormat.format("{0} Backup appender: [{1}].", prefix, appender.getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.ErrorHandler#setLogger(org.apache.log4j.Logger)
	 */
	public void setLogger(Logger logger) {
		if (attachables == null) {
			attachables = new ArrayList<AppenderAttachable>(1);
		}
		attachables.add(logger);
		LogLog.debug(MessageFormat.format("{0} Parent logger: [{1}]", prefix, logger.getName()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.log4j.spi.OptionHandler#activateOptions()
	 */
	public void activateOptions() {
	}

	/**
	 * Compensates for the lack of support for AppenderAttachable elements.
	 * 
	 * @param attachTo
	 */
	public void setAttachable(String name) {
		if (attachableNames == null) {
			attachableNames = new ArrayList<String>(2);
		}
		attachableNames.add(name);
		LogLog.debug(MessageFormat.format("{0} Named attachable: [{1}]", prefix, name));
	}

	/**
	 * @return
	 */
	protected boolean activate() {
		int replacements = 0;

		if (!StringUtil.isEmpty(attachableNames) && !StringUtil.isEmpty(attachables)) {
			for (String name : attachableNames) {
				for (int i = 0; i < attachables.size(); i++) {
					AppenderAttachable appatt = attachables.get(i);
					Appender app = appatt.getAppender(name);
					if ((app != null) && (app instanceof AppenderAttachable)) {
						appatt = (AppenderAttachable) app;
						app = appatt.getAppender(primary.getName());
						if (app == primary) {
							attachables.set(i, appatt);
							replacements++;
						}
					}
				}
			}

			return (replacements > 0);
		}

		return true;
	}

}