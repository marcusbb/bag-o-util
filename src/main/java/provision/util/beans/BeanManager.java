/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/beans/BeanManager.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.beans;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import provision.util.BaseException;
import provision.util.BaseUtil;
import provision.util.ReflectionException;
import provision.util.ReflectionUtil;
import provision.util.StringUtil;
import provision.util.ToolkitEvents;

/**
 * This class provides introspection utilities.
 * 
 * @author Iulian Vlasov
 */
public class BeanManager {

    public static final char               CHAINED_SEP       = '.';
    public static final char               INDEXED_SEP_START = '[';
    public static final char               INDEXED_SEP_END   = ']';
    public static final String             PROPERTY_CLASS    = "class";
    static final int                       DEFAULT_CAPACITY  = 10;

    private static Map<String, BeanHandle> beanHandles       = BaseUtil.createMap(DEFAULT_CAPACITY, true);
    private static Map<String, String>     beanAliases       = BaseUtil.createMap(DEFAULT_CAPACITY, true);

    /**
     * Iterates through all registered beans.
     * 
     * @return Iterator each element if a BeanHandle
     */
    public static Iterator<BeanHandle> beanIterator() {
        return beanHandles.values().iterator();
    }

    /**
     * Lookup a bean definition by name.
     * 
     * @parm name String alias of the bean
     * @return BeanHandle bean definition
     */
    public static BeanHandle lookup(String name) {
        String cls = beanAliases.get(name);
        if (cls != null) {
            return beanHandles.get(cls);
        }
        else {
            return beanHandles.get(name);
        }
    }

    /**
     * Register a bean from a String specifying its class. The bean is
     * registered under an alias identical with its class name.
     * 
     * @parm bClass String bean class name
     * @return BeanHandle bean definition
     * @exception BeanException if bean cannot be registered
     */
    public static BeanHandle registerBean(String bClass) throws BeanException {
        return registerBean(bClass, null);
    }

    /**
     * Register a bean from a String specifying its class. The bean is
     * registered under an alias.
     * 
     * @parm bClass String bean class name
     * @parm bName String bean alias
     * @return BeanHandle bean definition
     * @exception BeanException if bean cannot be registered
     */
    public static BeanHandle registerBean(String bClass, String bName) throws BeanException {
        if (BaseUtil.isEmpty(bClass)) return null;

        BeanHandle bHandle = (BeanHandle) lookup(bClass);
        if (bHandle == null) {
            try {
                if (BaseUtil.isEmpty(bName)) bName = bClass;

                Class<?> c = ReflectionUtil.newClass(bClass);
                BeanInfo info = Introspector.getBeanInfo(c);

                PropertyDescriptor[] pd = info.getPropertyDescriptors();
                Map<String, PropertyDescriptor> props = BaseUtil.createMap(pd.length - 1, true);

                for (int i = 0; i < pd.length; i++) {
                    String name = pd[i].getName();
                    if (name.equals(PROPERTY_CLASS)) continue;

                    props.put(name, pd[i]);
                }

                bHandle = new BeanHandle(bName, c, props);
                beanHandles.put(bClass, bHandle);

                if (!bName.equals(bClass)) {
                    beanAliases.put(bName, bClass);
                }
            }
            catch (IntrospectionException e) {
                throw new BeanException(ToolkitEvents.ERR_BEANS_INTROSPECTION, e, bClass);
            }
        }
        else {
            if (!BaseUtil.isEmpty(bName)) {
                if (!bHandle.getName().equals(bName)) {
                    beanAliases.put(bName, bHandle.getBeanClass().getName());
                }
            }

        }

        return bHandle;
    }

    /**
     * Register a bean from a Class object and returns a Map containing its
     * properties.
     * 
     * @parm bClass Class bean class
     * @return Map bean properties
     * @exception BeanException if bean cannot be registered
     */
    public static Map<String, PropertyDescriptor> getProperties(Class<?> bClass) throws BeanException {
        BeanHandle bHandle = registerBean(bClass.getName());
        return bHandle.getProperties();
    }

    /**
     * Register a bean from a Class name and returns a Map containing its
     * properties.
     * 
     * @parm bClass String bean class name
     * @return Map bean properties
     * @exception BeanException if bean cannot be registered
     */
    public static Map<String, PropertyDescriptor> getProperties(String bName) throws BeanException {
        BeanHandle bHandle = registerBean(bName, null);
        return bHandle.getProperties();
    }

    /**
     * Initialize a bean from a Map object. Each key is mapped to a bean
     * property. Keys may contain CHAINED_SEP tokens in which case the bean
     * property is itself a bean. Keys may contain INDEXED_SEP_START and
     * INDEXED_SEP_END tokens in which case the bean property is an indexed
     * property.
     * 
     * @parm bean Object bean instance
     * @parm Map map with property values
     * @exception BeanException if bean cannot be initialized
     */
    public static void setState(Object bean, Map<String, Object> map) throws BeanException {
        if (BaseUtil.isEmpty(map)) return;

        Map<String, PropertyDescriptor> props = getProperties(bean.getClass());

        for (String property : map.keySet()) {
            Object value = map.get(property);

            if ((property.indexOf(CHAINED_SEP) < 0) && (property.indexOf(INDEXED_SEP_START) < 0)) {

                // check straight writeable properties
                PropertyDescriptor pd = props.get(property);
                if ((pd == null) || (pd.getWriteMethod() == null)) continue;

                setProperty(bean, pd, value);
            }
            else {
                setProperty(bean, property, value);
            }
        }
    }

    /**
     * Obtain the value of a bean property. The property is specified by name.
     * 
     * @param bean Java bean object
     * @param pName Name of the property
     * @return Value of the property
     * @exception BeanException if the specified property is invalid
     */
    public static Object getProperty(Object bean, String pName) throws BeanException {
        Object value = null;
        String property = null;

        int sep = pName.indexOf(CHAINED_SEP);
        if (sep >= 0) {
            property = pName.substring(0, sep);
            Object beanProp = getProperty(bean, property, false);

            if (beanProp != null) {
                pName = pName.substring(sep + 1);
                value = getProperty(beanProp, pName);
            }
        }
        else {
            value = getProperty(bean, pName, false);
        }

        return value;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    public static Object getProperty(Object bean, String pName, boolean required) throws BeanException {
        String property = null;
        int index = 0;

        int ssep = pName.indexOf(INDEXED_SEP_START);
        if (ssep >= 0) {
            int esep = pName.indexOf(INDEXED_SEP_END);
            if (esep <= ssep) {
                throw new BeanException(new IllegalArgumentException(pName));
            }

            try {
                String sIndex = pName.substring(ssep + 1, esep);
                index = Integer.parseInt(sIndex);

                property = pName.substring(0, ssep);
            }
            catch (NumberFormatException e) {
                throw new BeanException(new IllegalArgumentException(pName));
            }
        }
        else {
            property = pName;
        }

        Map props = getProperties(bean.getClass());
        PropertyDescriptor pd = (PropertyDescriptor) props.get(property);
        if (pd == null) {
            throw new BeanException(new IllegalArgumentException(pName));
        }

        Object value = null;
        if (ssep >= 0) {
            try {
                value = getProperty(bean, pd, index);
            }
            catch (IndexOutOfBoundsException e) {
            }
        }
        else {
            value = getProperty(bean, pd);
        }

        if ((value == null) && required) {
            if (ssep >= 0) {
                boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);
                if (isIndexed) {
                    value = ReflectionUtil.newObject(((IndexedPropertyDescriptor) pd).getIndexedPropertyType());
                    setProperty(bean, pd, index, value);
                }
            }
            else {
                value = ReflectionUtil.newObject(pd.getPropertyType());
                setProperty(bean, pd, value);
            }
        }

        return value;
    }

    /**
     * Get the value of an object property. The property is specified by its
     * descriptor.
     * 
     * @param object Object target object whose property is to be set
     * @param descriptor PropertyDescriptor of the propery to be set
     * @return Object value of the object's property
     * @exception BeanException if fails to get the property
     */
    public static Object getProperty(Object bean, PropertyDescriptor pd) throws BeanException {
        try {
            Method m = pd.getReadMethod();
            if (m == null) {
                throw new IllegalAccessException(pd.getName());
            }

            return m.invoke(bean, new Object[0]);
        }
        catch (IllegalAccessException e) {
            throw new BeanException(e);
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof BeanException) {
                throw (BeanException) t;
            }
            else {
                throw new BeanException(t);
            }
        }
    }

    /**
     *
     */
    public static Object getProperty(Object bean, PropertyDescriptor pd, int index) throws BeanException {
        try {
            Object value = null;
            Method m = null;

            boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);
            if (isIndexed) {
                m = ((IndexedPropertyDescriptor) pd).getIndexedReadMethod();
                if (m == null) {

                    // no indexed getter, try non-indexed getter
                    m = pd.getReadMethod();
                    if (m == null) {
                        throw new BeanException(new IllegalAccessException(pd.getName()));
                    }

                    // try to get an array property
                    Object arProp = m.invoke(bean, new Object[0]);
                    if (!arProp.getClass().isArray()) {
                        throw new BeanException(new IllegalAccessException(pd.getName()));
                    }

                    value = Array.get(arProp, index);
                }

                value = m.invoke(bean, new Object[] { new Integer(index) });
            }
            else {
                throw new BeanException(new IndexOutOfBoundsException(pd.getName()));
            }

            return value;
        }
        catch (IllegalAccessException e) {
            throw new BeanException(e);
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof BeanException) {
                throw (BeanException) t;
            }
            else {
                throw new BeanException(t);
            }
        }
    }

    /**
     * Set the value of a bean property. The property is specified by name.
     * 
     * @param bean Java bean object
     * @param pName Name of the property
     * @param Value of the property
     * @exception BeanException if the specified property is invalid or the value is
     *                illegal.
     */
    public static void setProperty(Object bean, String pName, Object value) throws BeanException {
        String property = null;

        int sep = pName.indexOf(CHAINED_SEP);
        if (sep >= 0) {
            property = pName.substring(0, sep);
            Object beanProp = getProperty(bean, property, true);

            pName = pName.substring(sep + 1);
            setProperty(beanProp, pName, value);
        }
        else {
            int index = 0;

            int ssep = pName.indexOf(INDEXED_SEP_START);
            if (ssep >= 0) {
                int esep = pName.indexOf(INDEXED_SEP_END);
                if (esep <= ssep) {
                    throw new BeanException(new IllegalArgumentException(pName));
                }

                try {
                    String sIndex = pName.substring(ssep + 1, esep);
                    index = Integer.parseInt(sIndex);

                    property = pName.substring(0, ssep);
                }
                catch (NumberFormatException e) {
                    throw new BeanException(new IllegalArgumentException(pName));
                }
            }
            else {
                property = pName;
            }

            Map<String, PropertyDescriptor> props = getProperties(bean.getClass());
            PropertyDescriptor pd = (PropertyDescriptor) props.get(property);
            if (pd == null) {
                throw new BeanException(new IllegalArgumentException(pName));
            }

            if (ssep >= 0) {
                setProperty(bean, pd, index, value);
            }
            else {
                setProperty(bean, pd, value);
            }
        }
    }

    /**
     *
     */
    public static void setProperty(Object bean, PropertyDescriptor pd, Object value) throws BeanException {
        try {
            // writeable ?
            Method m = pd.getWriteMethod();
            if (m == null) {
                throw new BeanException(ToolkitEvents.ERR_BEANS_SETTER, bean.getClass(), pd.getName(), pd.getPropertyType());
            }

            // cast
            value = ReflectionUtil.cast(value, pd.getPropertyType());

            // set
            Throwable t = null;
            try {
                m.invoke(bean, new Object[] { value });
            }
            catch (IllegalAccessException e) {
                t = e;
            }
            catch (InvocationTargetException ite) {
                t = ite.getTargetException();
            }

            if (t != null) {
                String skey = "";

                if (t instanceof Exception) {
                    skey = ((Exception) t).getMessage();
                    t = ((Exception) t).getCause();
                }

                throw new BeanException(ToolkitEvents.ERR_BEANS_SETTER_FAILED, bean.getClass(), pd.getName(), pd.getPropertyType(), value);
            }
        }
        catch (BeanException bxcp) {
            bxcp.setPropertyName(pd.getName());
            bxcp.setPropertyType(pd.getPropertyType());
            bxcp.setValue(value);

            throw bxcp;
        }
    }

    /**
     * Set an object property to the specified value. The property is specified
     * by its descriptor.
     * 
     * @param object Object target object whose property is to be set
     * @param descriptor PropertyDescriptor of the propery to be set
     * @param value Object value to be store into the object's property
     * @exception BaseException if fails to set the property
     */
    public static void setProperty(Object bean, PropertyDescriptor pd, int index, Object value) throws BaseException {
        try {
            boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);
            Method m = null;

            if (isIndexed) {
                // writeable ?
                m = ((IndexedPropertyDescriptor) pd).getIndexedWriteMethod();
                if (m == null) {

                    // no indexed setter, try non-indexed getter
                    m = pd.getReadMethod();
                    if (m == null) {
                        throw new BeanException(ToolkitEvents.ERR_BEANS_SETTER, bean.getClass(), pd.getName(), pd.getPropertyType());
                    }

                    // try to get an array property
                    try {
                        Object arProp = _set(m, bean, new Object[0]);
                        if (!arProp.getClass().isArray()) {
                            throw new BeanException(
                                    "Unable to map value to bean property (property is read-only and NOT an array)");
                        }

                        Array.set(arProp, index, value);
                        return;
                    }
                    catch (Exception e) {
                        BeanException bxcp = new BeanException(
                                "Unable to map value to bean property (property is read-only and failed to GET array) ",
                                e);
                        throw bxcp;
                    }
                }

                // cast
                Class<?> propertyType = ((IndexedPropertyDescriptor) pd).getIndexedPropertyType();
                value = ReflectionUtil.cast(value, propertyType);

                // set
                try {
                    _set(m, bean, new Object[] { new Integer(index), value });
                }
                catch (Exception e) {
                    BeanException bxcp = new BeanException("Unable to map value to bean property (setter failed) ", e);
                    throw bxcp;
                }
            }
            else {
                throw new BeanException("Unable to map value to bean property (non-indexed property and index is "
                        + index);
            }
        }
        catch (BeanException bxcp) {
            if (bxcp.getPropertyName() == null) {
                bxcp.setPropertyName(pd.getName() + INDEXED_SEP_START + index + INDEXED_SEP_END);
                bxcp.setPropertyType(pd.getPropertyType());
                bxcp.setValue(value);
            }

            throw bxcp;
        }
    }

    /**
     *
     */
    public static String classToString(String cName) {
        StringBuffer sb = new StringBuffer();

        try {
            sb.append("Class Properties: ").append(cName).append(StringUtil.NEWLINE);
            Map<String, PropertyDescriptor> props = getProperties(cName);

            for (PropertyDescriptor pd : props.values()) {
                if (pd.getName().equals(PROPERTY_CLASS)) continue;

                boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);
                if (isIndexed) {
                    sb.append("  INDEXED PROPERTY=");
                }
                else {
                    sb.append("  SIMPLE PROPERTY=");
                }
                sb.append(pd.getName());

                boolean isRead = false, isWrite = false;
                String type = null;

                if (isIndexed) {
                    isRead = (((IndexedPropertyDescriptor) pd).getIndexedReadMethod() != null);
                    isWrite = (((IndexedPropertyDescriptor) pd).getIndexedWriteMethod() != null);
                    type = ((IndexedPropertyDescriptor) pd).getIndexedPropertyType().getName();
                }
                else {
                    isRead = (pd.getReadMethod() != null);
                    isWrite = (pd.getWriteMethod() != null);
                    type = pd.getPropertyType().getName();
                }

                sb.append("  TYPE=").append(type);
                sb.append("  ACCESS=").append(isRead && isWrite ? "read/write" : (isRead ? "read" : "write"));
                sb.append(StringUtil.NEWLINE);
            }
        }
        catch (Exception e) {
            sb.append(e);
        }

        return sb.toString();
    }

    /**
     *
     */
    public static String beanToString(Object bean) {
        StringBuffer sb = new StringBuffer();
        sb.append(bean.getClass().getName());

        beanToString(bean, "", sb);

        return sb.toString();
    }

    /**
     *
     */
    public static void beanToString(Object bean, String indent, StringBuffer sb) {
        String tab = "  ";

        try {
            Class<? extends Object> type = (bean == null ? null : bean.getClass());
            if ((bean == null) || isSimpleClass(type)) {
                sb.append("=").append(bean);
            }
            else if (Collection.class.isInstance(bean)) {
                int index = 0;
                for (Object elem : (Collection<?>) bean) {
                    sb.append(StringUtil.NEWLINE).append(indent).append(tab).append(index).append(". ");

                    if ((elem == null) || isSimpleClass(elem.getClass())) {
                        sb.append(elem);
                    }
                    else {
                        sb.append(elem.getClass());
                        beanToString(elem, indent + tab + tab, sb);
                    }

                    index++;
                }
            }
            else if ((bean != null) && type.isArray()) {
                int length = Array.getLength(bean);
                for (int index = 0; index < length; index++) {
                    Object elem = Array.get(bean, index);

                    sb.append(StringUtil.NEWLINE).append(indent).append(index).append(". ");

                    if ((elem == null) || isSimpleClass(elem.getClass())) {
                        sb.append(elem);
                    }
                    else {
                        beanToString(elem, indent + tab + tab, sb);
                    }
                }
            }
            else {
                // traverse this bean
                Map<String, PropertyDescriptor> props = getProperties(type);
                if (!props.isEmpty()) {
                    for (PropertyDescriptor pd : props.values()) {
                        String name = pd.getName();
                        if (PROPERTY_CLASS.equals(name)) continue;

                        boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);

                        // it's readable ?
                        if ((!isIndexed && (pd.getReadMethod() == null))
                                || (isIndexed && (((IndexedPropertyDescriptor) pd).getIndexedReadMethod() == null))) {

                            continue;
                        }

                        sb.append(StringUtil.NEWLINE).append(indent);
                        sb.append(" ");

                        if (isIndexed) {
                            sb.append(((IndexedPropertyDescriptor) pd).getIndexedPropertyType().getName());
                            sb.append(tab).append(name);

                            try {
                                for (int index = 0;; index++) {
                                    Object value = BeanManager.getProperty(bean, pd, index);

                                    sb.append(StringUtil.NEWLINE).append(indent).append(index).append(". ");
                                    beanToString(value, indent + tab + tab, sb);
                                }
                            }
                            catch (Exception e) {
                                Throwable t = e.getCause();
                                if (t instanceof IndexOutOfBoundsException) {
                                    // no more elements
                                }
                                else {
                                    System.err.println(e);
                                }
                            }
                        }
                        else {
                            String propType = pd.getPropertyType().getName();

                            sb.append(StringUtil.padRight(propType, 30));
                            sb.append(tab).append(":").append(name);

                            Object value = BeanManager.getProperty(bean, pd);
                            beanToString(value, indent + tab, sb);
                        }
                    } // for

                } // if isempty

            } // else
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private static Object _set(Method m, Object bean, Object[] values) throws BaseException {
        Throwable t = null;
        try {
            return m.invoke(bean, values);
        }
        catch (IllegalAccessException e) {
            throw new BaseException(e);
        }
        catch (InvocationTargetException ite) {
            t = ite.getTargetException();

            if (t instanceof BaseException) {
                throw (BaseException) t;
            }
            else {
                throw new BaseException("setter failed", t);
            }
        }
    }

    /**
     *
     */
    public static void traverseBean(Object bean, String parent, BeanPropertyCallback caller) {
        try {
            Class<? extends Object> type = (bean == null ? null : bean.getClass());
            if (parent == null) parent = "";
            StringBuffer ixName = new StringBuffer();

            if ((bean == null) || isSimpleClass(type)) {
                caller.process(parent, bean);
            }
            else if (Collection.class.isInstance(bean)) {
                int index = 0;
                for (Object elem : (Collection<?>) bean) {
                    ixName.setLength(0);
                    ixName.append(parent).append(INDEXED_SEP_START).append(index).append(INDEXED_SEP_END);

                    if ((elem == null) || isSimpleClass(elem.getClass())) {
                        caller.process(ixName.toString(), elem);
                    }
                    else {
                        traverseBean(elem, ixName.toString(), caller);
                    }
                    index++;
                }
            }
            else if ((bean != null) && type.isArray()) {
                int length = Array.getLength(bean);
                for (int index = 0; index < length; index++) {
                    Object elem = Array.get(bean, index);

                    ixName.setLength(0);
                    ixName.append(parent).append(INDEXED_SEP_START).append(index).append(INDEXED_SEP_END);

                    if ((elem == null) || isSimpleClass(elem.getClass())) {
                        caller.process(ixName.toString(), elem);
                    }
                    else {
                        traverseBean(elem, ixName.toString(), caller);
                    }
                }
            }
            else {
                // traverse this bean
                Map<String, PropertyDescriptor> props = getProperties(type);
                if (!props.isEmpty()) {
                    StringBuffer fullName = new StringBuffer();

                    for (PropertyDescriptor pd : props.values()) {
                        String name = pd.getName();
                        if (PROPERTY_CLASS.equals(name)) continue;

                        boolean isIndexed = (pd instanceof IndexedPropertyDescriptor);

                        // it's readable ?
                        if ((!isIndexed && (pd.getReadMethod() == null))
                                || (isIndexed && (((IndexedPropertyDescriptor) pd).getIndexedReadMethod() == null))) {

                            continue;
                        }

                        fullName.setLength(0);
                        if (parent.length() != 0) {
                            fullName.append(parent).append(".");
                        }
                        fullName.append(name);

                        if (isIndexed) {
                            try {
                                for (int index = 0;; index++) {
                                    Object value = BeanManager.getProperty(bean, pd, index);

                                    ixName.setLength(0);
                                    ixName.append(fullName).append(INDEXED_SEP_START).append(index).append(
                                            INDEXED_SEP_END);

                                    traverseBean(value, ixName.toString(), caller);
                                }
                            }
                            catch (Exception e) {
                                Throwable t = e.getCause();
                                if (t instanceof IndexOutOfBoundsException) {
                                    // no more elements
                                }
                                else {
                                    System.err.println(e);
                                }
                            }
                        }
                        else {
                            Object value = BeanManager.getProperty(bean, pd);

                            traverseBean(value, fullName.toString(), caller);
                        }
                    } // for

                } // if isempty

            } // else
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    public static boolean isSimpleClass(Class<?> type) {
        return (type.isPrimitive() || java.sql.Timestamp.class.isAssignableFrom(type) || (type.getName().startsWith(
                "java.")
                && !Collection.class.isAssignableFrom(type) && !type.isArray()));
    }

    public static String getAsProperty(StringBuffer property) {
        int ix = property.indexOf("_");
        if (ix == -1) return property.toString();

        int len = property.length();
        while (ix != -1) {
            if (ix + 1 < len) {
                char nextChar = property.charAt(ix + 1);
                property.deleteCharAt(ix);
                len--;
                property.setCharAt(ix, Character.toUpperCase(nextChar));

                ix = property.indexOf("_", ix + 1);
            }
            else {
                ix = -1;
            }
        }

        return property.toString();
    }

    public static String getAsProperty(String value) {
        return getAsProperty(value, true);
    }

    public static String getAsProperty(String value, boolean decap) {
        int ix = value.indexOf('_');
        if (ix == -1) return value;

        String property = getAsProperty(new StringBuffer(decap ? value.toLowerCase() : value));
        return property;
    }

    /**
     * @author Iulian Vlasov
     */
    public interface BeanPropertyCallback {

        /**
         * Do some specific processing on the specified property.
         * 
         * @parm fullName String full name of the property (including class
         *       name)
         * @parm value Object value of the property
         * @exception BaseException if value cannot be processed
         */
        public void process(String fullName, Object value) throws Exception;

    }

    /**
     * @param beanType
     * @param propName
     * @param propType
     * @return
     * @throws BeanException
     * @throws NoSuchMethodException
     */
    public static Method getGetter(Class<?> beanType, String propName, Class<String> propType) throws BeanException {
        Method getter = null;

        Map<String, PropertyDescriptor> props = getProperties(beanType);

        PropertyDescriptor pd = props.get(propName);
        if ((pd == null) || ((getter = pd.getReadMethod()) == null)
                || ((propType != null) && !pd.getPropertyType().isAssignableFrom(propType))) {
            
            throw new BeanException(ToolkitEvents.ERR_BEANS_GETTER, beanType, propName, propType);
        }

        return getter;
    }

    public static boolean propertyExists(Object bean, String pName) throws BeanException {
        return (getPropertyDescriptor(bean, pName) != null);
    }

    public static Object createProperty(Object bean, String pName) throws BeanException, ReflectionException {
        return createProperty(bean, pName, null);
    }

    public static Object createProperty(Object bean, String pName, String implClass) {
        PropertyDescriptor pd = getPropertyDescriptor(bean, pName);
        Class<?> pType = pd.getPropertyType();
        
        if (!StringUtil.isEmpty(implClass)) {
            return ReflectionUtil.newObject(implClass, pType);
        }
        else if (!pType.isInterface()) {
            return ReflectionUtil.newObject(pType);
        }
        else {
            return null;
        }
    }

    private static PropertyDescriptor getPropertyDescriptor(Object bean, String pName) throws BeanException {
        Map<String, PropertyDescriptor> props = getProperties(bean.getClass());
        return props.get(pName);
    }

    public static boolean setterExists(Object bean, String pName) throws BeanException {
        PropertyDescriptor pd = getPropertyDescriptor(bean, pName);
        return ((pd != null) && (pd.getWriteMethod() != null));
    }

    public static boolean isPropertyAssignableFrom(Object bean, String pName, Class<?> target) throws BeanException {
        PropertyDescriptor pd = getPropertyDescriptor(bean, pName);
        return ((pd != null) && pd.getPropertyType().isAssignableFrom(target));
    }

    public static Iterable<String> getSetters(Object bean) throws BeanException {
        BeanHandle bh = registerBean(bean.getClass().getName());
        final Iterator<PropertyDescriptor> values = bh.getProperties().values().iterator();

        return new Iterable<String>() {
            
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private PropertyDescriptor pd;

                    public boolean hasNext() {
                        while (values.hasNext()) {
                            this.pd = values.next();
                            if (pd.getWriteMethod() != null) {
                                return true;
                            }
                        }
                        
                        return false;
                    }

                    public String next() {
                        return pd.getName();
                    }

                    public void remove() {
                    }
                    
                };
            }
        };

    }

    /**
     * Injects the 
     * @param elem
     * @param bean
     */
    public static void setProperties(Element elem, Object bean) throws BeanException {
        NamedNodeMap attrs = elem.getAttributes();
        int aCount = attrs.getLength();
        
        for (int i = 0; i < aCount; i++) {
            Attr attr = (Attr) attrs.item(i);
            String aName = attr.getName();

            if (setterExists(bean, aName)) {
                String aValue = attr.getValue();
                setProperty(bean, aName, aValue);
            }
        }
    }    
    
}
