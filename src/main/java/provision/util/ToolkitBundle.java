/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ToolkitBundle.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.util.ResourceBundle;

/**
 * @author Iulian Vlasov
 * @since BA-Plus
 */
public class ToolkitBundle extends AbstractResourceBundle {

    private static ResourceBundle   bundle   = ResourceBundle.getBundle(ToolkitBundle.class.getName());

    private static final Object[][] contents = {
        // Resource Events
        { ToolkitEvents.INF_RESOURCE_URL.id, "Resource:[{0}] URL:[{1}] ResourceLocation:[{2}] ClassLoader:[{3}]" },
        { ToolkitEvents.INF_RESOURCE_BASEDIR.id, "Base Directory Setting: [{0}]=[{1}] Directory is: [{2}]"},
        { ToolkitEvents.ERR_RESOURCE_INVALID.id, "Unable to locate resource: [{0}]!" },
        { ToolkitEvents.ERR_RESOURCE_XML_CONFIG.id, "An XML parser configuration exception has occurred while processing resource: [{0}]!" },
        { ToolkitEvents.ERR_RESOURCE_XML_SAX.id, "The SAX parser was unable to parse the document: [{0}]!" },
        { ToolkitEvents.ERR_RESOURCE_IO.id, "An I/O error has occurred while parsing the document: [{0}]!" },
        { ToolkitEvents.ERR_RESOURCE_PERMISSION_DENIED.id, "Access to this resource is denied: {0}" },
        { ToolkitEvents.ERR_RESOURCE_NOT_FOUND.id, "Resource not found: {0}" },
        { ToolkitEvents.ERR_RESOURCE_LISTENER_FAILED.id, "Resource listener failed processing resource: {0}."},

        // Reflection Events
        { ToolkitEvents.ERR_REFLECT_INVALID_CLASS.id, "Class not found or invalid name: [{0}]"},
        { ToolkitEvents.ERR_REFLECT_TARGET_CLASS.id, "[{0}] is not of type [{1}]"},
        { ToolkitEvents.ERR_REFLECT_CONSTRUCTOR.id, "Unable to create instance of [{0}] - constructor invocation failed"},
        { ToolkitEvents.ERR_REFLECT_TARGET_OBJECT.id, "Will not create an instance of [{0}] because is not of type [{1}]"},
        { ToolkitEvents.ERR_REFLECT_INVOKE.id, "Unable to invoke method [{0}] on type [{1}]"},
        { ToolkitEvents.ERR_REFLECT_CONSTRUCTOR_ARGS.id, "Class [{0}] does not have a public constructor with parameter types: [{1}]!"},
        { ToolkitEvents.ERR_REFLECT_METHOD_ARGS.id, "Class [{0}] does not have a public method [{1}] with parameter types: [{2}]!"},
        { ToolkitEvents.ERR_REFLECT_CAST_ARRAY.id, "Unable to map multi-value array to a single value of type [{0}])"},
        { ToolkitEvents.ERR_REFLECT_CAST.id, "Unable to cast value [{0}] to type [{1}]!"},
        { ToolkitEvents.ERR_REFLECT_CAST_PROPERTY.id, "Invalid value [{0}] for property [{1}] of claSs [{2}]!"},

        // JavaBeans Events
        { ToolkitEvents.ERR_BEANS_INTROSPECTION.id, "Unable to introspect class [{0}]!"},
        { ToolkitEvents.ERR_BEANS_GETTER.id, "Class [{0}] does not have a readable property named [{1}] of type [{2}]!"},
        { ToolkitEvents.ERR_BEANS_SETTER.id, "Unable to map value to read-only bean property [{1}] of class [{0}]!"},
        { ToolkitEvents.ERR_BEANS_SETTER_FAILED.id, "Unable to invoke bean setter. Class: [{0}] Property name: [{1}] type:[{2}] value: [{3}]!"},

        { ToolkitEvents.NULL.id, "dummy" } };

    public Object[][] getContents() {
        return contents;
    }

    public static String get(String key, Object... args) {
        return AbstractResourceBundle.get(bundle, key, args);
    }

}