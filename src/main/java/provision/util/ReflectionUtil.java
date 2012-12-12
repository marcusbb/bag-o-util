/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ReflectionUtil.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides wrapper utility methods around Reflection and Introspection API.
 * 
 * @author Iulian Vlasov
 * @since PRV-5.0.3
 */
public class ReflectionUtil {


	private static Map<String, Class<?>> primitiveClassMap;

    static {
        PropertyEditorManager.registerEditor(boolean.class, BoolEditor.class);

		primitiveClassMap = new HashMap<String, Class<?>>();
		primitiveClassMap.put(int.class.getName(), int.class);
		primitiveClassMap.put(long.class.getName(), long.class);
		primitiveClassMap.put(double.class.getName(), double.class);
		primitiveClassMap.put(float.class.getName(), float.class);
		primitiveClassMap.put(char.class.getName(), char.class);
		primitiveClassMap.put(boolean.class.getName(), boolean.class);
		primitiveClassMap.put(byte.class.getName(), byte.class);
		primitiveClassMap.put(short.class.getName(), short.class);
    }
    
	
	public static Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException{
		Class<?> c = primitiveClassMap.get(name);
		if(c == null){
			c = Class.forName(name, false, classLoader);
		}
		
		return c;
	}
	

    /**
     * Return the class object associated with the fully qualified string name.
     * 
     * @param name
     * @return
     * @throws ReflectionException
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> newClass(String name) throws ReflectionException {
        try {
            return (Class<T>) Class.forName(name);
        }
        catch (Exception e) {
            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_INVALID_CLASS, e, name);
        }
    }

    /**
     * @param <T>
     * @param className
     * @param targetClass
     * @return
     * @throws ReflectionException - if the class associated with the given
     *             class name cannot be converted to the specified target class.
     */
    public static <T> Class<T> newClass(String className, Class<? super T> targetClass) throws ReflectionException {
        Class<T> srcClass = newClass(className);

        if ((targetClass == null) || targetClass.isAssignableFrom(Class.class)
                || targetClass.isAssignableFrom(srcClass)) return srcClass;

        if (targetClass.isArray()) {
            Class<?> componentType = targetClass.getComponentType();
            if (componentType.isAssignableFrom(Class.class) || componentType.isAssignableFrom(srcClass))
                return srcClass;
        }

        throw new ReflectionException(ToolkitEvents.ERR_REFLECT_TARGET_CLASS, className, targetClass);
    }

    /**
     * Instantiate an return an object whose class is associated with the fully
     * qualified string name and can be converted to the specified target class.
     * 
     * @param <T>
     * @param className
     * @param target
     * @return
     * @throws ReflectionException
     */
    public static <T> T newObject(String className, Class<T> target) throws ReflectionException {
        return newObject(className, target, (Object[]) null);
    }

    /**
     * Instantiate an return an object whose class is associated with the fully
     * qualified string name and can be converted to the specified target class.
     * 
     * @param <T>
     * @param className
     * @param target
     * @param args
     * @return
     * @throws ReflectionException
     */
    public static <T> T newObject(String className, Class<T> target, Object... args) throws ReflectionException {
        Class<T> source = newClass(className, target);
        return newObject(source, args);
    }

    /**
     * Instantiate an return an object of the given class. If the class
     * represents an interface or the Class type itself, the class object is
     * returned instead.
     * 
     * @param <T>
     * @param c
     * @return
     * @throws ReflectionException
     */
    public static <T> T newObject(Class<T> c) throws ReflectionException {
        return newObject(c, (Object[]) null);
    }

    /**
     * Create an implementation of the given class using a constructor that
     * matches the number and type of the given arguments.
     * 
     * @param <T>
     * @param c
     * @param args
     * @return
     * @throws ReflectionException
     */
    public static <T> T newObject(Class<T> c, Object... args) throws ReflectionException {
        Constructor<T> constr = findConstructor(c, args);
        try {
            return constr.newInstance(args);
        }
        catch (Exception e) {
            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CONSTRUCTOR, e, c);
        }
    }

    /**
     * Create an implementation of the given class using a constructor that
     * matches the number and type of the given arguments. If no constructor
     * matches the set of arguments, the no-args constructor is used.
     * 
     * @param <T>
     * @param c
     * @param args
     * @return
     * @throws ReflectionException
     */
    public static <T> T newDynamicObject(Class<T> c, Object... args) throws ReflectionException {
        T o = null;

        try {
            o = newObject(c, args);
        }
        catch (ReflectionException e) {
            if (BaseUtil.isRootCause(e, NoSuchMethodException.class) && (args != null)) {
                o = newObject(c);
            }
            else {
                throw e;
            }
        }

        return o;
    }

    /**
     * Instantiate an return an object whose class is associated with the fully
     * qualified string name and can be converted to the specified target class.
     * If no constructor matches the set of supplied arguments, the no-args
     * constructor is used.
     * 
     * @param <T>
     * @param className
     * @param target
     * @param args
     * @return
     * @throws ReflectionException
     */
    public static <T> T newDynamicObject(String className, Class<T> target, Object... args) throws ReflectionException {
        Class<T> source = newClass(className, target);
        return newDynamicObject(source, args);
    }

    /**
     * Instantiate an return an object of the given class which can be converted
     * to the specified target class. If the class represents an interface or
     * the Class type itself, the class object is returned instead. The
     * specified Object argument is used to inject the right parameters to the
     * object's class constructor. This implementation is able to create arrays
     * as well.
     * 
     * @param <T>
     * @param source
     * @param target
     * @param args
     * @return
     * @throws ReflectionException
     */
    @SuppressWarnings("unchecked")
    public static <T> T newObject(Class<T> source, Class<? super T> target, Object... args) throws ReflectionException {
        if (source.isInterface()) return (T) source;
        if (Class.class.isAssignableFrom(source) && !source.isAssignableFrom(Object.class)) return (T) source;

        if ((target != null) && target.isArray()) {
            Object component = null;

            Class<?> componentType = target.getComponentType();
            if (componentType.isAssignableFrom(Class.class)) {
                component = source;
            }
            else {
                component = newObject(source, args);
            }

            Object array = cast(component, target);
            return (T) array;
        }
        else {
            if (!target.isAssignableFrom(source)) {
                throw new ReflectionException(ToolkitEvents.ERR_REFLECT_TARGET_OBJECT, source, target);
            }

            return newObject(source, args);
        }
    }

    /**
     * Invoke the method specified by name on the given target object, using the
     * specified arguments.
     * 
     * @param <T>
     * @param target
     * @param name
     * @param args
     * @return
     * @throws ReflectionException
     */
    @SuppressWarnings("unchecked")
    public static <T> Object invoke(T target, String name, Object... args) throws ReflectionException {
        Class<T> c = (Class<T>) (target instanceof Class ? target : target.getClass());

        Method m = findMethod(c, name, args);
        try {
            return m.invoke(target, args);
        }
        catch (Exception e) {
            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_INVOKE, e, name, c);
        }
    }

    /**
     * Find an return the constructor of the given class whose parameters match
     * the number and type of the given arguments.
     * 
     * @param <T>
     * @param c
     * @param args
     * @return
     * @throws ReflectionException
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findConstructor(Class<T> c, Object... args) throws ReflectionException {
        int argCount = (args == null ? 0 : args.length);
        Object[] castArgs = null;
        int firstFitIndex = -1;

        Constructor<T>[] constr = (Constructor<T>[]) c.getConstructors();
        for (int i = 0; i < constr.length; i++) {
            Class<?>[] constrParms = constr[i].getParameterTypes();
            if (constrParms.length != argCount) continue;

            boolean bestFit = true;
            int j = 0;
            for (; j < constrParms.length; j++) {
                Class<?> argType = (args[j] == null ? Object.class : args[j].getClass());
                if (constrParms[j].isAssignableFrom(argType) || (args[j] == null)) continue;

                bestFit = false;
                break;
            }

            if (bestFit && (j == constrParms.length)) {
                return constr[i];
            }
        }

        if (firstFitIndex != -1) {
            System.arraycopy(castArgs, 0, args, 0, argCount);
            return constr[firstFitIndex];
        }
        else {
            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CONSTRUCTOR_ARGS, c.getName(), BaseUtil.toString(args));
        }
    }

    /**
     * Find an return the method of the given class whose parameters match the
     * number and type of the given arguments.
     * 
     * @param c
     * @param name
     * @param args
     * @return
     * @throws ReflectionException
     */
    public static Method findMethod(Class<?> c, String name, Object... args) throws ReflectionException {
        int argCount = (args == null ? 0 : args.length);

        Method[] methods = c.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].getName().equals(name)) continue;

            Class<?>[] methodParms = methods[i].getParameterTypes();
            if (methodParms.length != argCount) continue;

            int j = 0;
            for (; j < methodParms.length; j++) {
                Class<?> argType = (args[j] == null ? Object.class : args[j].getClass());
                if (methodParms[j].isAssignableFrom(argType) || (args[j] == null)) continue;

                args[j] = cast(args[j], methodParms[j]);
                if (!methodParms[j].isAssignableFrom(args[j].getClass())) break;
            }

            if (j == methodParms.length) {
                return methods[i];
            }
        }

        throw new ReflectionException(ToolkitEvents.ERR_REFLECT_METHOD_ARGS, c.getName(), name, BaseUtil.toString(args));
    }

    /**
     * Try to convert the given value to the given type. This implementation
     * handles arrays as well.
     * 
     * @param value
     * @param propertyType
     * @return
     * @throws ReflectionException
     */
    public static Object cast(Object value, Class<?> propertyType) throws ReflectionException {
        if (value == null) {
            if (propertyType.isPrimitive()) {

                // still try to create a default primitive object
                value = simpleCast("0", propertyType);
            }
        }
        else {
            Class<? extends Object> valueType = value.getClass();

            if (!propertyType.isAssignableFrom(valueType)) {
                boolean isArrayValue = valueType.isArray();
                boolean isArrayProperty = propertyType.isArray();

                // adjust one-element arrays to single objects and viceversa
                if (isArrayProperty) {
                    Object srcArray = null;

                    if (isArrayValue) {
                        srcArray = value;
                    }
                    else {
                        srcArray = Array.newInstance(valueType, 1);
                        Array.set(srcArray, 0, value);
                    }

                    if (propertyType.isAssignableFrom(srcArray.getClass())) {
                        value = srcArray;
                    }
                    else {
                        int nrElem = Array.getLength(srcArray);

                        Class<?> targetType = propertyType.getComponentType();
                        Object dstArray = Array.newInstance(targetType, nrElem);

                        for (int i = 0; i < nrElem; i++) {
                            Object element = Array.get(srcArray, i);
                            element = simpleCast(element, targetType);
                            Array.set(dstArray, i, element);
                        }

                        value = dstArray;
                    }
                }
                else {
                    if (isArrayValue) {
                        if (Array.getLength(value) == 1) {
                            value = Array.get(value, 0);
                        }
                        else {
                            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CAST_ARRAY, propertyType);
                        }
                    }

                    value = simpleCast(value, propertyType);
                }

            }
        } // value not null

        return value;
    }

    /**
     * Try to convert the given value to the given type.
     * 
     * @param srcValue
     * @param targetType
     * @return
     * @throws ReflectionException
     */
    public static Object simpleCast(Object srcValue, Class<?> targetType) throws ReflectionException {
        if ((srcValue instanceof String) && ((String) srcValue).length() == 0) {
            srcValue = null;
        }

        if (srcValue != null) {
            Class<? extends Object> srcType = srcValue.getClass();
            if (!targetType.isAssignableFrom(srcType)) {
                PropertyEditor pedit = PropertyEditorManager.findEditor(targetType);
                if (pedit != null) {
                    // use the editor
                    if (srcValue instanceof String) {
                        pedit.setAsText((String) srcValue);
                    }
                    else {
                        pedit.setAsText(srcValue.toString());
                    }

                    srcValue = pedit.getValue();
                }

                else {
                    try {
                        Constructor<?> constr = targetType.getConstructor(new Class[] { srcType });
                        srcValue = constr.newInstance(new Object[] { srcValue });
                    }
                    catch (Exception e) {
                        boolean castOk = false;
                        if (srcValue instanceof String) {
                            String className = (String) srcValue;

                            try {
                                if (Class.class.isAssignableFrom(targetType)) {
                                    srcValue = newClass(className);
                                    castOk = true;
                                }
                                else if (!targetType.isPrimitive()) {
                                    srcValue = newObject(className, targetType);
                                    castOk = true;
                                }
                            }
                            catch (Exception e1) {
                                throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CAST, e1, srcValue, targetType);
                            }
                        }

                        if (!castOk)
                            throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CAST, e, srcValue, targetType);
                    }
                }
            }
        }

        return srcValue;
    }

    /**
     * A bit of introspection used to set the state of the given JavaBean
     * object. The bidimensional array of properties specify the pairs of
     * property names and values. If this is all we need, we don't require the
     * use of a fully blown introspection library such as commons-beans.
     * 
     * @param bean
     * @param props
     */
    public static void setProperties(Object bean, String[][] props) throws ReflectionException {
        if ((bean == null) || (props == null) || (props.length == 0)) return;

        Class<? extends Object> clas = bean.getClass();
        BeanInfo bi;
        try {
            bi = Introspector.getBeanInfo(clas);
        }
        catch (IntrospectionException e) {
            //@TODO: move it in beans 
            throw new IllegalArgumentException(MessageFormat.format("Unable to get bean info for class {0}", clas), e);
        }

        PropertyDescriptor[] pd = bi.getPropertyDescriptors();
        for (int i = 0; i < props.length; i++) {
            if (props[i].length < 2) continue;

            String propName = props[i][0];
            String propValue = props[i][1];
            for (int j = 0; j < pd.length; j++) {
                if (!pd[j].getName().equals(propName)) continue;

                Method m = pd[j].getWriteMethod();
                if (m == null) continue;

                Object value = null;
                try {
                    value = cast(propValue, pd[j].getPropertyType());
                }
                catch (Exception e) {
                    throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CAST_PROPERTY, e, propValue, propName, clas);
                }

                try {
                    m.invoke(bean, value);
                }
                catch (Exception e) {
                    throw new ReflectionException(ToolkitEvents.ERR_REFLECT_CAST_PROPERTY, e, propValue, propName, clas);
                }
            }
        }
    }
    
	/**
	 * Retrieves an array of annotations for a particular parameter (indexed by @paramNo) on
	 * a given method (@method). 
	 * 
	 * @author Tran Huynh
	 * 
	 * @param method                   the method instance obtained from Java Reflection API for the given method
	 * 
	 * @param paramNo                  index of the parameter in the method starting with 0
	 * 
	 * @return null or an array of length 0 if the given parameter has no annotation
	 * 
	 * @throws IllegalArgumentException if the either the method is null OR the paramNo is less than 0
	 */
	public static Annotation[] getParameterAnnotations(Method method, int paramNo){
		if((method == null) || (paramNo < 0)){
			throw new IllegalArgumentException("getParameterAnnotations should not receive null method nor paramNo < 0");
		}
		
		Annotation[] paramAnnotations = null;
		Annotation[][] methodParamAnnotations = method.getParameterAnnotations();
		if((methodParamAnnotations != null) && (methodParamAnnotations.length > 0)){
			paramAnnotations = methodParamAnnotations[paramNo];
		}
		
		return paramAnnotations;
	}
	
	/**
	 * Retrieves the annotation that matches a particular type for a method parameter
	 * 
	 * @param method
	 * @param paramNo
	 * @param annotationClass
	 * @return
	 */
	public static <T> T getParameterAnnotation(Method method, int paramNo, Class<T> annotationClass){
		if((method == null) || (paramNo < 0) || (annotationClass == null)){
			throw new IllegalArgumentException("getParameterAnnotation should not receive null method, paramNo < 0 nor null annotationClass");
		}
		
		T typedAnnotation = null;
		
		Annotation[] paramAnnotations = getParameterAnnotations(method, paramNo);
		if((paramAnnotations != null) && (paramAnnotations.length > 0)){
			for(Annotation curAnnotation : paramAnnotations){
				if(annotationClass.isAssignableFrom(curAnnotation.annotationType())){
					return annotationClass.cast(curAnnotation);
				}
			}
		}
		
		return typedAnnotation;
	}

}
