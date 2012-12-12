/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/beans/BeanException.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.beans;

import provision.util.BaseEvent;
import provision.util.BaseException;

/**
 * @author Iulian Vlasov
 */
@SuppressWarnings("serial")
public class BeanException extends BaseException {
    private Class<?> type;
    private String   name;
    private Object   value;

    /**
     * @param evt
     * @param args
     */
    public BeanException(BaseEvent evt, Object... args) {
        super(evt, args);
    }

    /**
     * @param evt
     * @param t
     * @param args
     */
    public BeanException(BaseEvent evt, Throwable t, Object... args) {
        super(evt, t, args);
    }

    
    /**
     * Construct an exception with just a string message.
     * 
     * @param $message application-defined error message.
     */
    public BeanException(String message) {
        super(message);
    }

    /**
     * Construct an exception with a target exception.
     * 
     * @param $root error/exception that prevents normal operation.
     */
    public BeanException(Throwable root) {
        super(root);
    }

    /**
     * Construct an exception with a target exception and a string message.
     * 
     * @param $root error/exception that prevents normal operation.
     * @param $message application-defined error message.
     */
    public BeanException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Specify the type of the property associated with this exception.
     * 
     * @parm value Class property type
     */
    public void setPropertyType(Class<?> value) {
        this.type = value;
    }

    /**
     * Return the type of the property associated with this exception.
     * 
     * @return Class property type
     */
    public Class<?> getPropertyType() {
        return type;
    }

    /**
     * Specify the name of the property associated with this exception.
     * 
     * @parm value String property name
     */
    public void setPropertyName(String value) {
        this.name = value;
    }

    /**
     * Return the name of the property associated with this exception.
     * 
     * @return String property name
     */
    public String getPropertyName() {
        return name;
    }

    /**
     * Specify the desired value of the property associated with this exception.
     * 
     * @parm $value Object property value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Return the desired value of the property associated with this exception.
     * 
     * @return Object property value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Provide a string representation of this object.
     * 
     * @return String detailed information about the error.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("BEAN EXCEPTION:");

        String msg = getMessage();
        if (msg != null) {
            sb.append(" [message]=").append(msg);
        }

        sb.append(" [name]=").append(name);
        sb.append(" [type]=").append(type);
        sb.append(" [value]=").append(value);

        if (this.getCause() != null) {
            sb.append(" [root cause]=").append(getCause().getMessage());
        }

        return sb.toString();
    }

}
