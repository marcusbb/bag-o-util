/*
 * Created on May 31, 2005
 * 
 * (C) 2005 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 */
package provision.util;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static provision.util.JNDIUtilConstants.JMS_COMP_ENV_ROOT;
import static provision.util.JNDIUtilConstants.JMS_FACTORY;

/**
 * @author showard
 * 
 * Singleton instance used for sending messages to a specified JMS queue.  Handles the lookup 
 * of associated connection factory and destination objects and caches them.
 * 
 * Will attempt to use "wrapped" connection factories if they are available.
 * 
 */
public class JMSQueueSenderManager {
    //public final static String PROVISION_JMS_JNDI_CONTEXT_ROOT = "provision/jms/";
    
    private static String CALLER = "SenderManager";
    private static Pattern queueNamePattern = Pattern.compile("^\\w.*@(\\w+)$");

    private static JMSQueueSenderManager _instance = null;

    private Map queueDataCache = null;
    
    private JMSQueueSenderManager()
    {
        queueDataCache = Collections.synchronizedMap(new HashMap());
    }
    

    public static JMSQueueSenderManager getInstance()
    {
        JMSQueueSenderManager result = null;
     
        // could also use a synchornized method
        // but this is less synchronization overhead
        if (_instance != null) {
        	result = _instance;
        } else {
            synchronized (JMSQueueSenderManager.class) {
                if (_instance != null) {
                    result = _instance;
                } else {
                    result = _instance = new JMSQueueSenderManager();
                }
            }
        }
  
        return result;
    }
    
    // store thread-safe Queue-related objects
    public class QueueData
    {
        private Queue queue = null;
        private QueueConnectionFactory connectionFactory = null;
        private QueueConnection queueConnection = null;

        public QueueConnection getQueueConnection() {
            return queueConnection;
        }

        public void setQueueConnection(QueueConnection queueConnection) {
            this.queueConnection = queueConnection;
        }
        
        private boolean wrappedConnectionFactory = false;
        
        public QueueData()
        {
        }
        
        public QueueConnectionFactory getConnectionFactory() {
            return connectionFactory;
        }

        public Queue getQueue() {
            return queue;
        }
    
        public void setConnectionFactory(
                QueueConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public void setQueue(Queue queue) {
            this.queue = queue;
        }

        public boolean isWrappedConnectionFactory() {
            return wrappedConnectionFactory;
        }

        public void setWrappedConnectionFactory(boolean wrappedConnectionFactory) {
            this.wrappedConnectionFactory = wrappedConnectionFactory;
        }
    }
    
    public class JMSQueueSender{
    	private QueueConnection connection = null;
    	private QueueSession session = null;
    	private QueueSender sender = null;    	
    	private QueueData queueData = null;
    	private Queue destinationQueue = null;
    	boolean newConnection = false;
    	
    	//The constructor will set up the session, connection and the sender
    	public JMSQueueSender(String queueName, Queue queue, boolean createNewConnection, int acknowledgementMode) throws JMSException, NamingException {

    		newConnection = createNewConnection;
    		
    		// use supplied JMS queue if there is one
    		if (queueName == null && queue != null) {
    			queueName = getJNDIName(queue.getQueueName());
    		}

    		// get destination and CF objects - shared among threads
    		queueData = retrieveQueueData(queueName);

    		// use supplied JMS queue before cached queue
    		this.destinationQueue = (queue != null) ? queue : queueData.getQueue();

    		// should we check for NPE?

    		// create a connection - will be pooled if using a wrapped CF

    		connection = queueData.getQueueConnection();

    		// create a new connection if either
    		// 		(i) no connection exists, or
    		//		(ii) explicity specified to create a new connection
    		if (createNewConnection || connection == null) {

    			// close previously cached connection
    			if (connection != null) {
    				connection.close();
    			}

    			connection = queueData.getConnectionFactory().createQueueConnection();

    			if (!createNewConnection) {
    				// cache for later use
    				queueData.setQueueConnection(connection);
    			}
    		}

    		session = connection.createQueueSession(false, acknowledgementMode);

    		sender = session.createSender(destinationQueue);
    		
    	}
    	
    	private void sendMessage(Serializable message, String jmsType) throws JMSException{
            // will only handle two types so far - String and Object
            Message jmsMessage = null;
            if (message instanceof String) {
                jmsMessage = session.createTextMessage((String) message);
            } else {   
                jmsMessage = session.createObjectMessage(message);
            }
             
            if (jmsType != null) {
                jmsMessage.setJMSType(jmsType);
            }
             
            connection.start();

            sender.send(jmsMessage);

    	}

    	private void close() throws JMSException{
            if (sender != null) {
                sender.close();
            }
            
            if (session != null) {
                session.close();
            }
                
            if (newConnection && connection != null) {
                // closing the connection created by a "wrapped" CF, will return it to the connection pool managed by WL
                connection.close();
                queueData.setQueueConnection(null);		// remove from cache
            }
    	}   	    	
    }
    
    private void lookupConnectionFactory(QueueData queueData, InitialContext ic, String queueName) throws NamingException
    {
        QueueConnectionFactory queueConnectionFactory = null;
        boolean wrapped = false;
        
        // Attempt a lookup in the following order
        // 		(1) java:comp/env/jms/<queueName>ConnectionFactory (queue-specific wrapped CF)
        //		(2)	java:comp/env/jms/QueueConnectionFactory (wrapped CF for "general" use)
        //		(3) provision/jms/<queueName>ConnectionFactory (non-wrapped queue-specific CF)
        //		(4) default connection factory at "javax.jms.QueueConnectionFactory" (global default CF)
        
        String lookupName = null;
        
        lookupName = JMS_COMP_ENV_ROOT + queueName + "ConnectionFactory";
        try {
            queueConnectionFactory = (QueueConnectionFactory) ic.lookup(lookupName);
            wrapped = true;
        } catch (NamingException e) {
        }
        
        if (queueConnectionFactory == null) {
	        lookupName = JMS_COMP_ENV_ROOT + "QueueConnectionFactory";
	        wrapped = true;
	        try {
	            queueConnectionFactory = (QueueConnectionFactory) ic.lookup(lookupName);
	        } catch (NamingException e) {
	        }
        }

        /*
        if (queueConnectionFactory == null) {
	        lookupName = JNDINames.PROVISION_JMS_JNDI_CONTEXT_ROOT + queueName + "ConnectionFactory";
	        wrapped = false;
	        try {
	            queueConnectionFactory = (QueueConnectionFactory) ic.lookup(lookupName);
	        } catch (NamingException e) {
	        }
        }
	    */
        
        if (queueConnectionFactory == null) {
	        lookupName = JMS_FACTORY;
	        wrapped = false;
	        queueConnectionFactory = (QueueConnectionFactory) ic.lookup(lookupName);	// allow to throw Naming exception
        }
	        
        System.out.println(Thread.currentThread().getName() + ": using " + (wrapped ? "wrapped" : "non-wrapped") +
                	" connection factory bound to JNDI name '" + lookupName + "' for queue '" + queueName + "'");

        queueData.setConnectionFactory(queueConnectionFactory);
        queueData.setWrappedConnectionFactory(wrapped);
    }

    private QueueData retrieveQueueData(String queueName) throws NamingException
    {
        QueueData result = null;
        
        // do lookup in the cache, if not there
        if ((result = (QueueData) this.queueDataCache.get(queueName)) == null) {
 
            // could also use a synchronized method
            // but this is less synchronization overhead
            synchronized (this) {
                
                if ((result = (QueueData) this.queueDataCache.get(queueName)) == null) {
                    result = new QueueData();
                    
                    // do JNDI lookup of Queue and ConnectionFactory
	                InitialContext ic = new InitialContext();
	                
	                Queue queue = (Queue) ic.lookup(queueName);
	                
	                
	                result.setQueue(queue);
	                
	                lookupConnectionFactory(result, ic, queueName);
	                
	                // save in cache - should be thread-safe due to use of synchronized hash map
	                this.queueDataCache.put(queueName, result);
                }
            }
        }
        
        return result;
    }

    /**
     * Prior to WL10, the queueName returned by queue.getQueueName() is the same as
     * the queue's JNDI name. (Our configuration is such that the queue's JNDI name is
     * the same as the queue name). Since WL10, the queueName returned is different. It
     * looks something like "prv-jms-resource!PSJMSServer1001@RetriesQueue" where
     * "RetriesQueue" is the JNDI name. 
     */
    private String getJNDIName(String queueName)
    {
        String jndiName = queueName;
        if (queueName != null)
        {
            Matcher m = queueNamePattern.matcher(queueName);
            if (m.matches())
            {
                jndiName = m.group(1);
            }
        }
        return jndiName;
    }

    // internal method to send message once destination queue and CF have been resolved
    private void sendJMSMessage(Serializable message, String jmsType, String queueName,
            	Queue queue, int acknowledgementMode, boolean createNewConnection) throws NamingException, JMSException
    {
    	JMSQueueSender jmsSender = null;
    	try{
    		jmsSender = new JMSQueueSender(queueName, queue, createNewConnection, acknowledgementMode); 
        
    		jmsSender.sendMessage(message, jmsType);
        
        } catch (JMSException jmse) {
            // invalidate cache entries - remove associated CF and queue
            // this will force a lookup
            this.queueDataCache.remove(queueName);
            
            // still throw the exception anyway
            throw jmse;
    	} finally {
            if (jmsSender != null) {
            	jmsSender.close();
            }
        }        
    }
    
    /**
     * Sends a message for each item in the messages Collection
     * using the same connection, session and sender for the queue.
     *
     * @param Collection messages - a collection of serializable objects to be sent
     * @param String jmsType - used to set the JMS type of the message
     * @param String queueName - JNDI name where JMS queue is bound on local context
     * @param int acknowledgementMode - JMS acknowledgement mode - see javax.jms.Session or weblogic.jms.extensions.WLSession
     * @param boolean createNewConnection - specifies whether or not to create a new connection for each send.  Set this to "true"
     * 			if using either a wrapped connection factory or user connection factory.
     */
    public void sendBatchMessage(Collection<Serializable> messages, String jmsType, String queueName,
            	Queue queue, int acknowledgementMode, boolean createNewConnection) throws NamingException, JMSException
    {    	
    	JMSQueueSender jmsSender = null;
    	try{
    		jmsSender = new JMSQueueSender(queueName, queue, createNewConnection, acknowledgementMode); 
        
    		java.util.Iterator<Serializable> iter = messages.iterator();        
            while (iter.hasNext()){            	 
            	jmsSender.sendMessage(iter.next(), jmsType);
            }                		
        
        } catch (JMSException jmse) {
            // invalidate cache entries - remove associated CF and queue
            // this will force a lookup
            this.queueDataCache.remove(queueName);
            
            // still throw the exception anyway
            throw jmse;
    	} finally {
            if (jmsSender != null) {
            	jmsSender.close();
            }
        }        
    }

    public void sendBatchMessage(Collection<Serializable> messages, String jmsType, String queueName) throws NamingException, JMSException{
    	sendBatchMessage(messages, jmsType, queueName, null, Session.AUTO_ACKNOWLEDGE, false);
    }

    // Public API methods
    
    /**
     * Send a object message to the specified JMS queue bound to JNDI queueName.
     * 
     * @param Serializable message - the (serializable) object to be sent
     * @param String jmsType - used to set the JMS type of the message
     * @param String queueName - JNDI name where JMS queue is bound on local context
     * @param int acknowledgementMode - JMS acknowledgement mode - see javax.jms.Session or weblogic.jms.extensions.WLSession
     * @param boolean createNewConnection - specifies whether or not to create a new connection for each send.  Set this to "true"
     * 			if using either a wrapped connection factory or user connection factory. 
     */
    public void sendMessage(Serializable message, String jmsType, String queueName, int acknowledgementMode, boolean createNewConnection) throws NamingException, JMSException
    {
        sendJMSMessage(message, jmsType, queueName, null, acknowledgementMode, createNewConnection);
                
    }    
 
    /**
     * Send a text message to the specified JMS queue bound to JNDI queueName.
     * 
     * @param String message - the text to be sent as JMS TextMessage
     * @param String jmsType - used to set the JMS type of the message
     * @param String queueName - JNDI name where JMS queue is bound on local context
     * @param int acknowledgementMode - JMS acknowledgement mode - see javax.jms.Session or weblogic.jms.extensions.WLSession 
     * @param boolean createNewConnection - specifies whether or not to create a new connection for each send.  Set this to "true"
     * 		if using either a wrapped connection factory or user connection factory.  
     */
    public void sendMessage(String message, String jmsType, String queueName, int acknowledgementMode, boolean createNewConnection) throws NamingException, JMSException
    {
        sendJMSMessage(message, jmsType, queueName, null, acknowledgementMode, createNewConnection);
    }
}
