/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/AbstractResourceBundle.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author Iulian Vlasov
 * @since BA-Plus
 */
public abstract class AbstractResourceBundle extends ListResourceBundle {

    private static Map<String, MessageFormat> allFormats     = new HashMap<String, MessageFormat>(20);

    private static final String               defaultMessage = "Message not translated:[{0}]! Arguments:[{1}]";

    public static String get(ResourceBundle bundle, String key, Object... args) {
        try {
            String msg = bundle.getString(key);
            if (args != null) {
                MessageFormat msgFormat = (MessageFormat) allFormats.get(key);
                if (msgFormat == null) {
                    msgFormat = new MessageFormat(msg);
                    allFormats.put(key, msgFormat);
                }

                msg = msgFormat.format(args);
            }

            return msg;
        }
        catch (MissingResourceException e) {
            return MessageFormat.format(defaultMessage, key, BaseUtil.toString(args));
        }
    }

    /**
     * @see java.util.ListResourceBundle#getContents()
     */
    protected abstract Object[][] getContents();

}
