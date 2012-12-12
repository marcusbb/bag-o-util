/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/DefaultSequenceConverter.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Converts the custom conversion character {@link PSPatternLayout#SEQUENCE_CONVERSION} in order to
 * add a sequential counter to the formatted message. The counter ranges from 0000 - 9999.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class DefaultSequenceConverter extends PatternConverter {

	/**
	 * The sequential count for a stream of messages belonging to one process.
	 */
	private int seq = 0;

	/**
	 * Returns the current sequence number.
	 * 
	 * @param event - not used
	 * @return
	 */
	@Override
	protected String convert(LoggingEvent event) {
		StringBuilder b = new StringBuilder();
		if (seq > 9999) seq = 0;

		int th = seq / 1000;
		int s = th * 1000;
		b.append(th);
		int h = (seq - s) / 100;
		b.append(h);
		s += h * 100;
		int t = (seq - s) / 10;
		b.append(t);
		s += t * 10;
		int o = (seq - s);
		b.append(o);
		seq++;
		return b.toString();
	}
}