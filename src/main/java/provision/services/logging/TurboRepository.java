/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboRepository.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RepositorySelector;

/**
 * @author Iulian Vlasov
 * @since  TurboPrv
 */
public class TurboRepository extends Hierarchy {

	static final String SELECTOR_GETTER = "getSelector";
	
	private RepositorySelector selector;

	public TurboRepository(Logger root, RepositorySelector selector) {
		super(root);
		this.selector = selector;
	}

	public RepositorySelector getSelector() {
		return selector;
	}

}