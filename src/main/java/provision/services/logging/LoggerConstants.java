package provision.services.logging;

/**
 * The LoggerConstants interface
 *
 * @author		Snezana Visnjic-Obucina
 * @version
 * @see
 * @since
 */

public interface LoggerConstants {

    // Configuration file
    String LOGGING_CONFIG_FILE = "configFile.lcf";

	// delay in milliseconds to wait between each check for updates in the configuration file
    public static final long CONFIG_READ_DELAY = 300000; 

    // Logging severity constants
    int FATAL_INT = 50000;
    int ERROR_INT = 40000;
    int WARN_INT  = 30000;
    int INFO_INT  = 20000;
    int DEBUG_INT = 10000;

	// log4j MDC (Mapped Diagnostic Context) keys
	String ORIG_THREAD_KEY = "Orig_Thread";
	String ORIG_TIME_STAMP = "Orig_Time_Stamp";

	public static final String EVENT_TOKEN = "EVENT";
    public static final char VALUE_SEP = '=';
    public static final char PAIR_SEP = ';';
    public static final String MSG_TOKEN = "EVENT_MSG";
    public static final String TEXT_DELIM = "\"";
    public static final char PAIR_SEP_REPLACEMENT_CHAR = '!';
    public static final String EXCEPTION_TOKEN = "EVENT_EXCEPTION";
    public static final char SPACE = ' ';
    public static final int LINE_WRAP_LENGTH = 255;
    public static final int LOG_HEADER_LENGTH = 110; // approximately

    //public static final String PRINTABLE_NEWLINE = "'\\n'";
    public static final String DEFAULT_EOL_DELIM = "|^|";
}

