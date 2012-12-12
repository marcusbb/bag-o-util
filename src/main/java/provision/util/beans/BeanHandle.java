/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/beans/BeanHandle.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.beans;

import java.util.Map;
import java.beans.PropertyDescriptor;

/**
 * This class encapsulates information about a bean.
 * 
 * @author Iulian Vlasov
 */
public class BeanHandle {

    private String name;
    private Class<?> clas;
    private Map<String,PropertyDescriptor> props;
    
    /**
     * Construct a BeanHandle with a name, a type and a Map with properties.
     *
     * @param name String name/alias for the bean
     * @param c Class bean type
     * @param props Map map of property descriptors
     */
    public BeanHandle(String name, Class<?> c, Map<String,PropertyDescriptor> props) {
        this.name = name;
        this.clas = c;
        this.props = props;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return clas;
    }

    public Map<String,PropertyDescriptor> getProperties() {
        return props;
    }

    public PropertyDescriptor getProperty(String name) {
        return props.get(name);
    }

}