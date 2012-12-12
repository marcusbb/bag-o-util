/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/XMLUtil.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an utility class providing XML convenience methods.
 * 
 * @author Iulian Vlasov
 * @since PRV-5.0.3
 */
public class XMLUtil {

    private static final String CLASS_ATTR = "class";

    /**
     * @param resource
     * @param validate
     * @return
     * @throws Exception
     */
    public static Element getDocumentElement(String resource, boolean validate) throws Exception {
        InputStream input = null;

        try {
            input = ResourceUtil.getResourceAsStream(resource);
            if (input == null) {
                throw new IllegalArgumentException("Unable to load resource: " + resource);
            }

            return getDocumentElement(input, validate);
        }
        finally {
            try {
                if (input != null) input.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    /**
     * @param resource
     * @param validate
     * @return
     * @throws Exception
     */
    public static Element getDocumentElement(InputStream resource, boolean validate) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validate);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(resource);

        return document.getDocumentElement();
    }

    /**
     * @param elem
     * @param childTag
     * @return
     */
    public static Element getFirstChild(Element elem, String childTag) {
        if (elem.hasChildNodes()) {
            NodeList list = elem.getElementsByTagName(childTag);
            int count = list.getLength();

            for (int i = 0; i < count; i++) {
                Node node = list.item(i);
                if (node.getParentNode() != elem) continue;

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    return (Element) node;
                }
            }
        }

        return null;
    }

    /**
     * @param <T>
     * @param parent
     * @param tagName
     * @param cb
     * @return
     */
    public static <T> List<T> createList(Element parent, String tagName, NodeListCallback<T> cb) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        int count = nodeList.getLength();

        List<T> l = cb.createList(tagName, count);

        for (int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if ((node.getParentNode() == parent) && (node.getNodeType() == Node.ELEMENT_NODE)) {
                cb.addElement((Element) node, l);
            }
        }

        return l;
    }

    public static <K,V> Map<K,V> createMap(Element parent, String tagName, NodeMapCallback<K, V> cb) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        int count = nodeList.getLength();

        Map<K, V> m = cb.createMap(tagName, count);

        for (int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if ((node.getParentNode() == parent) && (node.getNodeType() == Node.ELEMENT_NODE)) {
                cb.addElement((Element) node, m);
            }
        }

        return m;
    }

    public static Map<String,String> createMap(Element parent, String tagName, final String nameAttr, final String valueAttr) {

        Map<String, String> vars = createMap(parent, tagName, new NodeMapCallback<String, String>() {

            public void addElement(Element elem, Map<String, String> m) {
                String name = elem.getAttribute(nameAttr);
                String value = elem.getAttribute(valueAttr);
                m.put(name, value);
            }

            public Map<String, String> createMap(String tagName, int size) {
                return BaseUtil.createMap(size);
            }

        });

        return vars;
    }

    /**
     * @param parent
     * @param tagName
     * @param cb
     */
    public static void forEachElement(Element parent, String tagName, NodeCallback cb) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        int count = nodeList.getLength();

        for (int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if ((node.getParentNode() == parent) && (node.getNodeType() == Node.ELEMENT_NODE)) {
                cb.processElement((Element) node);
            }
        }
    }

    /**
     * @param parent
     * @param name
     * @param caller
     */
    public static void processElements(Element parent, String name, XMLCallback caller) {
        NodeList nodeList = parent.getElementsByTagName(name);
        int count = nodeList.getLength();

        caller.setElementCount(name, count);

        for (int i = 0; i < count; i++) {
            Node node = nodeList.item(i);
            if ((node.getParentNode() == parent) && (node.getNodeType() == Node.ELEMENT_NODE)) {
                caller.processElement((Element) node);
            }
        }
    }

    public interface NodeListCallback<T> {

        public List<T> createList(String tagName, int size);

        public void addElement(Element elem, List<T> l);

    }

    public interface NodeMapCallback<K, V> {

        public Map<K, V> createMap(String tagName, int size);

        public void addElement(Element elem, Map<K, V> m);

    }

    public interface NodeCallback {

        public void processElement(Element elem);

    }

    public interface XMLCallback extends NodeCallback {

        void setElementCount(String elemName, int value);

    }

    public static <T> T newInstance(Element elem, Class<T> target, Class<? extends T> defImpl, Object... args) {
        T inst = null;

        String fqcn = elem.getAttribute(CLASS_ATTR);
        if (StringUtil.isEmpty(fqcn)) {
            if (defImpl != null) {
                inst = ReflectionUtil.newDynamicObject(defImpl, args);
            }
        }
        else {
            inst = ReflectionUtil.newDynamicObject(fqcn, target, args);
        }

        return inst;
    }

}