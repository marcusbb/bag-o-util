/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/PSPatternParser.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;

/**
 * Custom pattern parser that can handle any number of custom conversion characters. It uses a
 * convertion factory to obtain the appropriate pattern converter.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class PSPatternParser extends PatternParser implements CustomConverterSupport {

	private ConverterFactory convFactory;

	/**
	 * @param pattern
	 */
	public PSPatternParser(String pattern) {
		super(pattern);
	}

	/**
	 * Handles any number of custom conversion characters by using the pattern converter factory. If
	 * the factory does not return a valid converter, it will use the default converter provided by
	 * the superclass.
	 * 
	 * @param c
	 * @see org.apache.log4j.helpers.PatternParser#finalizeConverter(char)
	 */
	@Override
	public void finalizeConverter(char c) {
		PatternConverter converter = null;

		if ((convFactory != null) && ((converter = convFactory.getConverter(c)) != null)) {
			addConverter(converter);
		}
		else {
			super.finalizeConverter(c);
		}
	}

	/**
	 * Enable injection of a converter factory.
	 * 
	 * @param cf
	 */
	public void setConverterFactory(ConverterFactory cf) {
		this.convFactory = cf;
	}
}