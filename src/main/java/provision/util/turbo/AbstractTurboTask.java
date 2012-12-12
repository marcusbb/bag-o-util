/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/turbo/AbstractTurboTask.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util.turbo;

/**
 * This is the abstract implementation of a turbo task.
 * 
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public abstract class AbstractTurboTask<T> implements TurboTask<T> {

	protected T taskData;

	public AbstractTurboTask(T data) {
		this.taskData = data;
	}

	public void run() {
		doTask(taskData);
	}

}
