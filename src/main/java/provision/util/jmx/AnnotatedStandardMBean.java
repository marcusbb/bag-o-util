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

import java.lang.reflect.Method;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import provision.util.JMXUtil;
import provision.util.ReflectionUtil;

/**
 * An implementation of the Standard MBean that populate each operation's 
 * parameter name based on the value provided in the PName annotation
 * 
 * {@link provision.util.jmx.PName}
 * 
 * In order for MBean operation to display correct information, operations
 * defined in the MBean Interface must have its parameter annotated with proper
 * label via the PName annotation.
 * 
 * @author trhuynh
 *
 */
public class AnnotatedStandardMBean extends StandardMBean{
	
	/**
	 * @param mbeanInterface
	 * @throws NotCompliantMBeanException
	 */
	protected AnnotatedStandardMBean(Class<?> mbeanInterface) throws NotCompliantMBeanException {
		super(mbeanInterface);
	}

	protected String getParameterName(MBeanOperationInfo operationInfo, MBeanParameterInfo paramInfo, int paramNo){
		String value = null;
		
		if((operationInfo != null) && (paramInfo != null) && (paramNo >= 0)){

			try {
				Method operationMethod = JMXUtil.getMethodForOperation(getMBeanInterface(), operationInfo);
				PName pname = ReflectionUtil.getParameterAnnotation(operationMethod, paramNo, PName.class);
				value = pname.value();
			} 
			catch (SecurityException e) {
				e.printStackTrace();
			} 
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return ((value == null) || ("".equals(value)))
		       ? super.getParameterName(operationInfo, paramInfo, paramNo)
		       : value;
		
	}
}
