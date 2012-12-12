/*
 * (C) 2010 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/util/ResourceWatcher.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.util;

import java.io.File;
import java.net.URL;

import provision.services.logging.Logger;

/**
 * @author Iulian Vlasov
 */
public class ResourceWatcher extends Thread {
	private static final String CALLER = ResourceWatcher.class.getName();
	
    static final long          DEFAULT_WATCH_DELAY = 60000;

    protected long             watchDelay          = DEFAULT_WATCH_DELAY;
    protected URL              resUrl;
    protected File             resFile;
    protected long             lastModTime;
    protected boolean          warnedOnce;
    protected boolean          cancelled;
    protected ResourceListener listener;
    protected boolean          hasListener;

    /**
     * @param f
     * @param delay
     * @param listener
     */
    public ResourceWatcher(URL url, long delay, ResourceListener l, String name) {
        this.resUrl = url;
        this.resFile = new File(url.getFile());

        StringBuilder sb = new StringBuilder(name).append('-').append(SystemUtil.getContextName());
        setName(sb.toString());
        
        setWatchDelay(delay);
        setDaemon(true);

        watch();
        this.listener = l;
    }

    /**
     * Set the delay to observe between each check of the file changes.
     */
    public void setWatchDelay(long delay) {
        if (delay > 0) {
            this.watchDelay = delay;
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    /**
     * 
     */
    protected void watch() {
        boolean exists;
        try {
            exists = resFile.exists();
        }
        catch (SecurityException e) {
            Logger.error(CALLER, ToolkitBundle.get(ToolkitEvents.ERR_RESOURCE_PERMISSION_DENIED.id, resFile), e);
            cancelled = true;
            return;
        }

        if (exists) {
            long curTime = resFile.lastModified();
            if (curTime > lastModTime) {
                lastModTime = curTime;
                
                try {
                    if (listener != null) {
                        listener.onChange(resUrl);
                    }
                }
                catch (Exception e) {
                    // don't want to stop monitoring just because of misbehaving listeners 
                    Logger.error(CALLER, ToolkitBundle.get(ToolkitEvents.ERR_RESOURCE_LISTENER_FAILED.id, resUrl), e);
                }

                warnedOnce = false;
            }
        }
        else {
            if (!warnedOnce) {
                Logger.warn(CALLER, ToolkitBundle.get(ToolkitEvents.ERR_RESOURCE_NOT_FOUND.id, resFile));
                warnedOnce = true;
            }
        }
    }

    /**
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
        while (!cancelled) {
            try {
                Thread.sleep(watchDelay);
            }
            catch (InterruptedException ignored) {
            }

            watch();
        }
    }

}