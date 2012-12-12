/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ResourceUtil.java#1 $
 * $DateTime: 2012/12/12 09:03:29 $ $Author: msimonsen $ $Change: 6006354 $
 */
package provision.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Contains utility methods to manipulate resources such as files and streams.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class ResourceUtil {

	/**
	 * If this system property is set, it denotes the base directory relative to which all file
	 * references are resolved. If not set, file locations are relative to the JVM's current
	 * directory.
	 */
	public static final String APP_BASEDIR = "app.basedir";

	/**
	 * Useful to specify absolute paths instead of relative ones.
	 */
	public static final String FILE_URL = "file://";

	/**
	 * If this system property is set, enables logging of extra information.
	 */
	public static final String APP_VERBOSE_LOGGING = "app.logging.verbose";

	/**
	 * Uses the classloader hierarchy to locate a resource by name. If the resource is not found by
	 * any classloader, it tries to locate it in the filesystem using an absolute or relative path
	 * to the "base" or "current" directory.
	 * 
	 * @param name
	 * @return URL of the resource or null if the resource could not be located.
	 * @see #getFileSystemResource(String)
	 */
	public static URL getResourceAsURL(String name) {
		ResourceLocation rl = ResourceLocation.NotFound;
		ClassLoader cl = null;
		URL url = null;

		if ((url = Thread.currentThread().getContextClassLoader().getResource(name)) == null) {
			if ((url = ResourceUtil.class.getClassLoader().getResource(name)) == null) {
				if ((url = ClassLoader.getSystemResource(name)) == null) {
					File f = getFileSystemResource(name);
					if (f != null) {
						try {
							url = f.toURI().toURL();
							rl = ResourceLocation.FileSystem;
						}
						catch (MalformedURLException e) {
							// @NOTE: should not happen as the file and the
							// protocol handler both exist
							e.printStackTrace();
						}
					}
				}
				else {
					rl = ResourceLocation.SystemClassLoader;
					cl = ClassLoader.getSystemClassLoader();
				}
			}
			else {
				rl = ResourceLocation.ApplicationClassLoader;
				cl = ResourceUtil.class.getClassLoader();
			}
		}
		else {
			rl = ResourceLocation.ContextClassLoader;
			cl = Thread.currentThread().getContextClassLoader();
		}

		if (Boolean.getBoolean(APP_VERBOSE_LOGGING)) {
			System.out.println(MessageFormat.format("[getResourceAsURL] [resource]={0} [url]={1} [loader]={2}:{3}", name,
					url, rl, cl));
		}

		return url;
	}

	/**
	 * Locates a resource by name and returns a File reference to it. The search is done using the
	 * classloader hierarchy and then falling back to the filesystem, using an absolute or relative
	 * path to the "base" or "current" directory.
	 * 
	 * @param name
	 * @return File whose pathname points to the resource or null if the resource could not be
	 *         located.
	 * @see #getResourceAsURL(String)
	 */
	public static File getResourceAsFile(String name) {
		URL url = getResourceAsURL(name);
		if (url == null) return null;

		File file = new File(url.getFile());
		return (file.exists() ? file : null);
	}

	/**
	 * Locates a resource by name and returns an InputStream for reading it. The search is done
	 * using the classloader hierarchy and then falling back to the filesystem, using an absolute or
	 * relative path to the "base" or "current" directory.
	 * 
	 * @param name
	 * @return An input stream for reading the resource, or null if the resource could not be
	 *         located.
	 * @throws IOException if cannot open the stream from the resource's URL.
	 * @see #getResourceAsURL(String)
	 */
	public static InputStream getResourceAsStream(String name) throws IOException {
		URL url = getResourceAsURL(name);
		if (url == null) return null;

		return url.openStream();
	}

	/**
	 * Locates a resource by name and returns a Reader for reading it. The search is done using the
	 * classloader hierarchy and then falling back to the filesystem, using an absolute or relative
	 * path to the "base" or "current" directory.
	 * 
	 * @param name
	 * @return A reader for reading the resource, or null if the resource could not be located.
	 * @throws IOException if cannot open the reader from the resource's stream.
	 * @see #getResourceAsStream(String)
	 */
	public static Reader getResourceAsReader(String name) throws IOException {
		InputStream is = getResourceAsStream(name);
		if (is == null) return null;

		return new InputStreamReader(is);
	}

	/**
	 * Locates a resource by name and loads its content as a Properties object.
	 * 
	 * @param name
	 * @return Properties object or null if the resource could not be located or its content could
	 *         not be read as properties.
	 * @see #getResourceAsStream(String)
	 */
	public static Properties getResourceAsProperties(String name) {
		try {
			return getResourceAsProperties(getResourceAsStream(name), null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load the contents of a file as a Properties object.
	 * 
	 * @param f
	 * @return Properties object or null if the file could not be located or its content could not
	 *         be read as properties.
	 * @see #getResourceAsStream(String)
	 */
	public static Properties getResourceAsProperties(File f) {
		try {
			return getResourceAsProperties(new FileInputStream(f), null);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load the content of the input stream as a Properties object. Optionally, it uses the
	 * specified properties arguments as properties defaults.
	 * 
	 * @param is
	 * @param defaults
	 * @return Properties object or null if the input stream's content could not be read as
	 *         properties.
	 */
	public static Properties getResourceAsProperties(InputStream is, Properties defaults) {
		Properties p = null;

		try {
			if (is != null) {
				p = (defaults == null ? new Properties() : new Properties(defaults));
				p.load(is);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) try {
				is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		return p;
	}

	/**
	 * Locates a resource by name and loads its content as text.
	 * 
	 * @param name
	 * @return Content of the resource as String object or null if the resource could not be located
	 *         or its content could not be read.
	 * @throws IOException
	 * @see #getResourceAsReader(String)
	 */
	public static String getResourceAsText(String name) throws IOException {
		Reader reader = getResourceAsReader(name);
		if (reader == null) return null;

		BufferedReader breader = new BufferedReader(reader);
		StringWriter out = new StringWriter();
		Writer writer = new BufferedWriter(out);

		char[] buf = new char[4096];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			writer.write(buf, 0, numRead);
		}

		reader.close();
		breader.close();
		writer.close();
		return out.toString();
	}

	/**
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static byte[] getResourceAsBytes(String name) {
		byte[] data = null;
		InputStream is = null;
		
		try {
			is = getResourceAsStream(name);
			if (is != null) {
				data = getResourceAsBytes(is);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) try {
				is.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] getResourceAsBytes(InputStream is) throws IOException {
		return getResourceAsBytes(is, Long.MAX_VALUE);
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] getResourceAsBytes(InputStream is, long maxBytes) throws IOException {
		BufferedInputStream in = new BufferedInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		byte[] buf = new byte[4096];
		int numRead = 0;
		int totalRead = 0;
		while ((totalRead < maxBytes) && (numRead = in.read(buf)) != -1) {
			totalRead += numRead;
			if (totalRead > maxBytes) {
				numRead -= (totalRead - maxBytes);
			}
			out.write(buf, 0, numRead);
		}

		in.close();
		out.close();
		
		return out.toByteArray();
	}
	
	/**
	 * Returns a File reference to a resource specified by name. If the resource name starts with
	 * {@link #FILE_URL} it is considered an absolute path. Otherwise, the name is considered to be
	 * relative to the base directory.
	 * 
	 * @param name
	 * @return File whose pathname points to the resource or null if the file does not exist.
	 * @see #getBaseDirectory()
	 */
	public static File getFileSystemResource(String name) {
		if (name.startsWith(FILE_URL)) {
			// absolute path
			File file = new File(name.substring(FILE_URL.length()));
			return (file.exists() ? file : null);
		}

		// relative to the base directory
		return getFileSystemResource(name, getBaseDirectory());
	}

	/**
	 * Returns a File reference to a resource specified by name, relative to a base directory.
	 * 
	 * @param name
	 * @param basedir
	 * @return File whose pathname points to the resource or null if the file does not exist.
	 * @see #getBaseDirectory()
	 */
	public static File getFileSystemResource(String name, File basedir) {
		if (basedir == null) {
			basedir = getBaseDirectory();
		}

		File file = new File(basedir, name);
		return (file.exists() ? file : null);
	}

	/**
	 * The base directory is specified using the {@link #APP_BASEDIR} system property. If it exists,
	 * it should point to a valid directory in the filesystem used to resolve all relative path
	 * references. If the system property does not exist or it points to an invalid directory, it
	 * uses the current directory as the base directory. The current directory is the directory from
	 * where the JVM was started.
	 * 
	 * @return File directory, relative to which all relative path are resolved.
	 */
	public static File getBaseDirectory() {
		String baseDirProp = null;
		File baseDir = null;

		try {
			baseDirProp = System.getProperty(APP_BASEDIR);
			if (!StringUtil.isEmpty(baseDirProp)) {
				baseDir = new File(baseDirProp);
				if (baseDir.isDirectory()) return baseDir;
			}

			baseDir = new File(new File("").getAbsolutePath());
			return baseDir;
		}
		finally {
			if (Boolean.getBoolean(APP_VERBOSE_LOGGING)) {
				System.out.println(MessageFormat.format("[getBaseDirectory] [{0}]={1} [dir]={2}", APP_BASEDIR,
						baseDirProp, baseDir));
			}
		}
	}

	enum ResourceLocation {
		ContextClassLoader, ApplicationClassLoader, SystemClassLoader, FileSystem, NotFound
	}

	/**
	 * @param f
	 * @param format
	 * @return
	 */
	public static String getFormattedFileTime(File f, String format) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(f.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(cal.getTime());
		}
		catch (SecurityException ignored) {
			return "";
		}

	}

    /**
     * @param url
     * @return
     * @throws BaseException
     */
    public static Document getResourceAsDocument(URL url) throws BaseException {
        try {
            InputStream is = url.openStream();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(is);
        }
        catch (IOException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_IO, e, url);
        }
        catch (ParserConfigurationException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_XML_CONFIG, e, url);
        }
        catch (SAXException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_XML_SAX, e, url);
        }
    }
	
    public static Document getResourceAsDocument(String name) throws BaseException {
        File file = getResourceAsFile(name);
        if (file == null) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_INVALID, name);
        }

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse (file);
        }
        catch (ParserConfigurationException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_XML_CONFIG, e, name);
        }
        catch (SAXException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_XML_SAX, e, name);
        }
        catch (IOException e) {
            throw new BaseException(ToolkitEvents.ERR_RESOURCE_IO, e, name);
        }
    }

}
