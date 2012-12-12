/**
 * (C) 2012 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * File Id: $Id$
 * Author: Tran Huynh
 * Updated by: $Author$ 
 * Last modified: $DateTime$ 
 * Recent Change List: $Change$
 */
package provision.util.jmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * An annotation for storing the name of a parameter for a Standard MBean.
 * 
 * When JConsole displays parameter names for a Standard MBean, it default the name
 * to "p" prefix followed by the parameter count, starting from 1, so for example
 * it looks like this: p1, p2, p3, etc..
 * 
 * which is not descriptive enough for end user to understand what each parameter
 * value means, so the whole purpose of this annotation is to provide developer a
 * way to annotate a descriptive parameter name to be displayed on JConsole for each
 * parameter in the Standard MBean that they'd like to expose.
 * 
 * This annotation only works if its used in conjunction with the 
 * 
 * {@link test.tools.AnnotatedStandardMBean}
 * 
 * implementation.
 * 
 * @author trhuynh
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PName {
    String value();
}
