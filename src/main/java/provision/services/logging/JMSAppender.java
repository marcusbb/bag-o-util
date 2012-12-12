/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/JMSAppender.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import provision.util.ResourceUtil;

/**
 * Custom appender that works with either JMS messaging model. Connection properties are
 * individually specified or via properties file.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class JMSAppender extends AppenderSkeleton {

	public final static String DEFAULT_CONNECTION_FACTORY = "weblogic.jms.ConnectionFactory";
	public final static String DEFAULT_DESTINATION = "LogMessageQueue";

	protected String jndiProperties;
	protected String initialContextFactory;
	protected String providerUrl;
	protected String destinationName = DEFAULT_DESTINATION;
	protected String connectionFactoryName = DEFAULT_CONNECTION_FACTORY;
	protected boolean requiresLayout = true;

	protected Connection connection;
	protected Session session;
	protected MessageProducer producer;

	public JMSAppender() {
	}

	/**
	 * The <b>InitialContextFactory</b> option takes a string value. Its value, along with the
	 * <b>ProviderUrl</b> option will be used to get the InitialContext.
	 */
	public void setInitialContextFactory(String s) {
		this.initialContextFactory = s;
	}

	/**
	 * Returns the value of the <b>InitialContextFactory</b> option.
	 */
	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	/**
	 * The <b>ProviderUrl</b> option takes a string value. Its value, along with the
	 * <b>InitialContextFactory</b> option will be used to get the InitialContext.
	 */
	public void setProviderUrl(String s) {
		this.providerUrl = s;
	}

	/**
	 * Returns the value of the <b>ProviderUrl</b> option.
	 */
	public String getProviderUrl() {
		return providerUrl;
	}

	/**
	 * The <b>ConnectionFactoryName</b> option takes a string value. Its value will be used to
	 * lookup the appropriate <code>ConnectionFactory</code> from the JNDI context.
	 */
	public void setConnectionFactoryName(String s) {
		this.connectionFactoryName = s;
	}

	/**
	 * Returns the value of the <b>ConnectionFactoryName</b> option.
	 */
	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	/**
	 * The <b>DestinationName</b> option takes a string value. Its value will be used to lookup the
	 * appropriate <code>Destination</code> from the JNDI context.
	 */
	public void setDestinationName(String s) {
		this.destinationName = s;
	}

	/**
	 * Returns the value of the <b>DestinationName</b> option.
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * @param s the jndi properties to help creating the jndi context
	 */
	public void setJndiProperties(String s) {
		this.jndiProperties = s;
	}

	/**
	 * @return the jndiProperties
	 */
	public String getJndiProperties() {
		return jndiProperties;
	}

	/**
	 * @param requiresLayout the requiresLayout to set
	 */
	public void setRequiresLayout(boolean requiresLayout) {
		this.requiresLayout = requiresLayout;
	}

	/**
	 * @return the requiresLayout flag
	 */
	public boolean getRequiresLayout() {
		return requiresLayout();
	}

	/**
	 * Initialize the JMS infrastructure.
	 */
	public void activateOptions() {

		try {
			Context ctx = getContext();

			ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup(connectionFactoryName);
			Destination destination = (Destination) ctx.lookup(destinationName);

			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(destination);

			connection.start();
			ctx.close();
		}
		catch (Exception e) {
			errorHandler.error(new StringBuilder("Could not activate options for JMSAppender: ").append(name)
					.toString(), e, ErrorCode.GENERIC_FAILURE);
		}
	}

	public boolean requiresLayout() {
		return this.requiresLayout;
	}

	/**
	 * This method called by {@link AppenderSkeleton#doAppend} method to do most of the real
	 * appending work. The LoggingEvent will be be wrapped in an ObjectMessage or it will be
	 * formatted by the layout. and wrapped in an TextMessage to be sent to the JMS destination.
	 */
	public void append(LoggingEvent event) {
		try {
			Message msg = session.createObjectMessage();

			if (requiresLayout()) {
				int iLevel = event.getLevel().toInt();
				// String caller = event.getLoggerName();
				String caller = (String) event.getMDC(Logger.CALLER_LOGGER);
				String message = layout.format(event);

				//*** This repository has to be loaded by the same classloader as the logging MDB *** 
				LoggingMessage lm = new LoggingMessage(iLevel, caller, message);

				lm.setOrigTimeStamp(event.getMDC(Logger.CALLER_TIMESTAMP));
				lm.setOrigThread(event.getMDC(Logger.CALLER_THREAD));
				
				((ObjectMessage) msg).setObject(lm);
			}
			else {
				((ObjectMessage) msg).setObject(event);
			}

			producer.send(msg);
		}
		catch (Exception e) {
			errorHandler.error(new StringBuilder("Could not send message in JMSAppender: ").append(name).toString(), e,
					ErrorCode.GENERIC_FAILURE);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	public void close() {
		synchronized (this) {
			if (closed) {
				return;
			}
			closed = true;
		}

		try {
			if (session != null) session.close();
			if (connection != null) connection.close();
		}
		catch (Exception e) {
			LogLog.error("Error while closing JMSAppender: " + name, e);
		}

		producer = null;
		session = null;
		connection = null;
	}

	/**
	 * All jndi settings required to create the initial context are loaded from the specified file
	 * or only the factory and provider by individual setters.
	 * 
	 * @return
	 * @throws NamingException
	 */
	protected Context getContext() throws NamingException {
		Properties p = null;

		if (jndiProperties != null) {
			p = ResourceUtil.getResourceAsProperties(jndiProperties);
			if (p == null) {
				LogLog.error("Could not load initial context properties: " + jndiProperties);
			}
		}

		if (p == null) {
			p = new Properties();
		}

		try {
			if (initialContextFactory != null) {
				p.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
			}
			else {
				initialContextFactory = p.getProperty(Context.INITIAL_CONTEXT_FACTORY);
			}

			if (providerUrl != null) {
				p.put(Context.PROVIDER_URL, providerUrl);
			}
			else {
				providerUrl = p.getProperty(Context.PROVIDER_URL);
			}

			Context ctx = (p.isEmpty() ? new InitialContext() : new InitialContext(p));
			return ctx;
		}
		catch (NamingException ne) {
			LogLog.error(new StringBuilder("Could not get initial context with factory: ")
					.append(initialContextFactory).append(" and provider: ").append(providerUrl).toString());
			throw ne;
		}
	}

}