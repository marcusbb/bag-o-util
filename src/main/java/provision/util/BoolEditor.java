/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/BoolEditor.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.beans.PropertyEditorSupport;

/**
 * @author Iulian Vlasov
 */
public class BoolEditor extends PropertyEditorSupport {
	
	private boolean bValue;
	
	public BoolEditor() {
		super();
	}
	
	public BoolEditor(Object source) {
		super(source);
	}
	
	@Override
	public void setAsText(String text) {
		bValue = "1".equals(text) || "true".equalsIgnoreCase(text) || "yes".equalsIgnoreCase(text);
	}
	
	@Override
	public Object getValue() {
		return bValue;
	}
	
}
