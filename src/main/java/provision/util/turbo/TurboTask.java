/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/TurboTask.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

/**
 * This interface separates the logical action of running a business task from the physical action
 * of executing it within its own thread/runnable.
 * 
 * The task is performed against a specific input type.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public interface TurboTask<T> extends Runnable {

	/**
	 * 
	 */
	void doTask(T taskData);

}
