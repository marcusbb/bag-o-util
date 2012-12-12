package provision.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * A utility to extract version from classpath/along with other build properties
 * 
 * It will issue a {@link #NO_VERSION} if no build properties can be found.
 * This is to prevent runtime exceptions in the client {@link NullPointerException}
 * 
 * 
 * @author msimonsen
 *
 */
public class Version {

    public static String NO_VERSION = "NO_VER";
    private static String version = null;
        
    private static Properties properties = null;
    public static String VERSION_KEY = "Prv-version";
    /**
     * As some application servers (Weblogic namely)
     * disallows reading ANY type of manifest file 
     * from any classloader, we are forced into the properties
     * paradigm.
     * This mainly becomes build substitution, or dynamic
     * property generation during build time
     */
    public static String manifest = "build.properties";
    
    static {
        try {
            InputStream ins = ResourceUtil.getResourceAsStream(manifest);
            if (ins!= null) {
                
                properties = new Properties();
                properties.load(ins);
                version = properties.getProperty(VERSION_KEY);
            }else {
                
                version = NO_VERSION;                           
            }
        }catch(IOException e) {
            version = NO_VERSION;
        }
    }
    /**
     * Returns a version number from the Manifest file with a particular attribute
     * called {@link #VERSION_KEY} 
     * 
     * @return
     */
    public static String version() {
        
        return version;
    }
    
    public static String buildDate() {
        return properties.getProperty("build-date");
    }
    
}
