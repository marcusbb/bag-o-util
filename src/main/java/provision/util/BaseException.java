/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/BaseException.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the root of the exception hierarchy. It exposes two generic
 * attributes:
 * <ul>
 * <li>id - String identifier uniquely associated with a certain type of
 * exception. Subclasses will define their own id. This will make it easy to
 * search in the logs for occurrences of a certain subtype of this base
 * exception.</li>
 * <li>context - Map that stores named arguments and their values. These
 * elements describe the context available at the time the exception occurred.</li>
 * <p>
 * 
 * @author Iulian Vlasov
 * @since BA-Plus
 */
public class BaseException extends RuntimeException {

    private static final long     serialVersionUID = -8069593138370674106L;
    private static final Pattern  formatPattern    = Pattern.compile("(\\{\\d+\\})");

    protected String              msg;
    protected BaseEvent           event;
    protected Map<String, Object> context;

    /**
     * @param message
     */
    public BaseException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates the exception from a specific event, a root cause exception and
     * an arbitrary number of contextual arguments.
     * 
     * @param evt - The event associated with this exception. The exception
     *            message is the message of the given event.
     * @param t - The cause exception that is wrapped by this exception.
     * @param args - The arguments are expected to be specified in pairs, with
     *            the first element being the argument name and the second one
     *            the argument value.
     */
    public BaseException(BaseEvent evt, Throwable t, Object... args) {
        super(evt.getMessage(), t);
        this.event = evt;
        this.context = getContext(args);
    }

    /**
     * Creates the exception from a specific event and an arbitrary number of
     * contextual arguments.
     * 
     * @param evt - The event associated with this exception. The exception
     *            message is the message of the given event.
     * @param args - The arguments are expected to be specified in pairs, with
     *            the first element being the argument name and the second one
     *            the argument value.
     */
    public BaseException(BaseEvent evt, Object... args) {
        this(evt, (Throwable) null, args);
    }

    /**
     * Creates an exception associated with the given event.
     * 
     * @param evt - The event associated with this exception. The exception
     *            message is the message of the given event.
     */
    public BaseException(BaseEvent evt) {
        this(evt, (Throwable) null, (Object[]) null);
    }

    /**
     * Return the unique identifier associated with this exception.
     * 
     * @return
     */
    public String getId() {
        return getEvent().getId();
    }

    /**
     * Return the context available at the time this exception occurred.
     * 
     * @return
     */
    public Map<String, Object> getContext() {
        return (context == null ? null : Collections.unmodifiableMap(context));
    }

    /**
     * Each subclass will specify its own event by either overriding this method
     * or by setting the protected <code>event</code> at the time of
     * constructing the exception.
     * 
     * @return
     */
    protected BaseEvent getEvent() {
        return event;
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return (msg == null ? super.getMessage() : msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ErrMesg>: ").append(getMessage());

        if (this.event != null) {
            StringUtil.appendLine(sb, "<ErrName>: ").append(event.getId());
            StringUtil.appendLine(sb, "<ErrCode>: ").append(event.getCode());

            if (!BaseUtil.isEmpty(context)) {
                StringUtil.appendLine(sb, "<ErrCntx>:");
                for (Object key : context.keySet()) {
                    Object value = context.get(key);
                    sb.append(" [").append(key).append("]=").append(value);
                }
            }
        }

        return sb.toString();
    }

    /**
     * @param args
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getContext(Object... args) {
        if (args == null) return null;

        Map<String, Object> ctx = null;
        if ((args.length == 1) && Map.class.isAssignableFrom(args[0].getClass())) {
            ctx = (Map) args[0];
        }
        else {
            String msg = getMessage();
            int parmCount = getParameterCount(msg);

            if (parmCount > 0) {
                this.msg = MessageFormat.format(msg, args);
            }
            else {
                // build event parameters map
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) continue;

                    String key = (String) args[i];
                    if (i < args.length - 1) {
                        Object value = args[++i];
                        if (ctx == null) {
                            ctx = new OrderedMap<String, Object>((int) ((args.length - i + 1) / 0.75));
                        }

                        ctx.put(key, value);
                    }
                }
            }
        }

        return ctx;
    }

    public boolean isCausedBy(Class<?> xcp) {
        Throwable cause = this;
        while (cause != null) {
            if (cause.getClass().isAssignableFrom(xcp)) {
                return true;
            }

            if (cause != cause.getCause()) {
                cause = cause.getCause();
            }
            else {
                cause = null;
            }
        }

        return false;
    }

    /**
     * Used to detect if the event message is formatted and has to be combined
     * with the event parameters.
     * 
     * @param input
     * @return
     */
    private int getParameterCount(String input) {
        Matcher m = formatPattern.matcher(input);
        int count = 0;
        while (m.find()) {
            count++;
        }

        return count;
    }

}
