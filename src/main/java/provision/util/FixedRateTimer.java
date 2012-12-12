/**
 * (C) 2012 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * @author Tran Huynh
 */
package provision.util;

import java.util.Timer;
import java.util.TimerTask;

import provision.services.logging.Logger;

/**
 * A general purpose timer that schedules a task based on fixed delay algorithm
 * as defined by {@link java.util.Timer#scheduleAtFixedRate}.
 * 
 * It keeps a reference to the Timer and TimerTask that it was given
 * as well as all initialization parameters that it was provided.
 * 
 * @author trhuynh
 *
 */
public class FixedRateTimer {
	
	private final static String CALLER = FixedRateTimer.class.getName();
	
	private final static long MILLIS_PER_SECOND = 1000;
	
	private Timer timer;
	private TimerTask timerTask;
	private long initialDelayInSeconds;
	private long scheduleIntervalInSeconds;
	
	public FixedRateTimer(TimerTask timerTask, long initialDelayInSeconds, long scheduleIntervalInSeconds){
		this.timer = new Timer();
		this.timerTask = timerTask;
		this.initialDelayInSeconds = initialDelayInSeconds;
		this.scheduleIntervalInSeconds = scheduleIntervalInSeconds;
	}
	
	public void schedule(){
		Logger.info(CALLER, "schedule_Begin",
				    "Scheduling task",
				    "DELAY_BEFORE_EXEC_IN_SEC", initialDelayInSeconds,
				    "REPEATABLE_INTERVAL_IN_SEC", scheduleIntervalInSeconds);
		
		timer.scheduleAtFixedRate(timerTask, initialDelayInSeconds * MILLIS_PER_SECOND, scheduleIntervalInSeconds * MILLIS_PER_SECOND);
	}
	
	public void cancel(){
		Logger.info(CALLER, "schedule_Cancellation_Begin",
				    "Cancelling all tasks",
				    "DELAY_BEFORE_EXEC_IN_SEC", initialDelayInSeconds,
				    "REPEATABLE_INTERVAL_IN_SEC", scheduleIntervalInSeconds);
		
		timer.cancel();
	}

	/**
	 * @return the timerTask
	 */
	public TimerTask getTimerTask() {
		return timerTask;
	}

	/**
	 * @return the initialDelay
	 */
	public long getInitialDelay() {
		return initialDelayInSeconds;
	}

	/**
	 * @return the scheduleInterval
	 */
	public long getScheduleInterval() {
		return scheduleIntervalInSeconds;
	}
}
