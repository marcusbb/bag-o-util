/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ToolkitEvents.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;


/**
 * Collection of basic toolkit events.
 * 
 * @author Iulian Vlasov
 * @since BA-Plus
 */
public enum ToolkitEvents implements BaseEvent {

    // Resource Events
    INF_RESOURCE_URL             (Range.RESOURCE.valueOf( 0)),
    INF_RESOURCE_BASEDIR         (Range.RESOURCE.valueOf( 1)),
    ERR_RESOURCE_INVALID         (Range.RESOURCE.valueOf( 2)), 
    ERR_RESOURCE_XML_CONFIG      (Range.RESOURCE.valueOf( 3)),
    ERR_RESOURCE_XML_SAX         (Range.RESOURCE.valueOf( 4)), 
    ERR_RESOURCE_IO              (Range.RESOURCE.valueOf( 5)),
    ERR_RESOURCE_PERMISSION_DENIED (Range.RESOURCE.valueOf( 6)),
    ERR_RESOURCE_NOT_FOUND       (Range.RESOURCE.valueOf( 7)),
    ERR_RESOURCE_LISTENER_FAILED (Range.RESOURCE.valueOf( 8)),


    // Reflection Events
    ERR_REFLECT_INVALID_CLASS    (Range.RESOURCE.valueOf(20)),
    ERR_REFLECT_TARGET_CLASS     (Range.RESOURCE.valueOf(21)),
    ERR_REFLECT_CONSTRUCTOR      (Range.RESOURCE.valueOf(22)),
    ERR_REFLECT_TARGET_OBJECT    (Range.RESOURCE.valueOf(23)),
    ERR_REFLECT_INVOKE           (Range.RESOURCE.valueOf(24)),
    ERR_REFLECT_CONSTRUCTOR_ARGS (Range.RESOURCE.valueOf(25)),
    ERR_REFLECT_METHOD_ARGS      (Range.RESOURCE.valueOf(26)),
    ERR_REFLECT_CAST_ARRAY       (Range.RESOURCE.valueOf(27)),
    ERR_REFLECT_CAST             (Range.RESOURCE.valueOf(28)),
    ERR_REFLECT_CAST_PROPERTY    (Range.RESOURCE.valueOf(29)),

    // JavaBeans Events
    ERR_BEANS_INTROSPECTION      (Range.RESOURCE.valueOf(40)),
    ERR_BEANS_GETTER             (Range.RESOURCE.valueOf(41)),
    ERR_BEANS_SETTER             (Range.RESOURCE.valueOf(42)),
    ERR_BEANS_SETTER_FAILED      (Range.RESOURCE.valueOf(43)),
    
	NULL();

	protected static final String APP_ERROR_PREFIX = "TK";

	public final String id = name();
	public final String code;
	private String message;

	ToolkitEvents() {
		this.code = "";
	}

	ToolkitEvents(int _codeIndex) {
		this.code = APP_ERROR_PREFIX + _codeIndex;
	}

	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		if ((message == null) && (id != null)) {
			this.message = ToolkitBundle.get(id);
		}

		return message;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name());
		sb.append('(').append(id);
		sb.append(',').append(code);
		sb.append(',').append(getMessage());
		sb.append(')');
		return sb.toString();
	}

	/**
	 *
	 */
	enum Range {

		RESOURCE("Resource Utilities", 1),
		NULL();

		protected static final int RANGE_SIZE = 1000;
		protected static final int BASE_START = 0;

		public final String description;
		public final int start;

		Range() {
			this("", 0);
		}

		Range(String _description, int _rangeIndex) {
			this.description = _description;
			this.start = BASE_START + (_rangeIndex * RANGE_SIZE);
		}

		int valueOf(int codeIndex) {
			return start + codeIndex;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(name());
			sb.append('(').append(description);
			sb.append(',').append(start);
			sb.append(')');
			return sb.toString();
		}
	}

	public static void main(String[] args) {
		for (Range cr : Range.values()) {
			System.out.println(cr);
		}

		for (BaseEvent e : ToolkitEvents.values()) {
			System.out.println(e);
		}
	}

}