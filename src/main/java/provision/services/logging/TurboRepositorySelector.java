/*
 * (C) 2009 Research In Motion Ltd. All Rights Reserved.
 * RIM, Research In Motion -- Reg. U.S. Patent and Trademark Office
 * The RIM Logo and Inter@ctive are trademarks of Research In Motion, Limited
 * All materials confidential information of Research In Motion, Limited
 *
 * $Id: //depot/dev/cardhu/IES/common/master/util/src/main/java/provision/services/logging/TurboRepositorySelector.java#1 $
 * $Author: msimonsen $ $DateTime: 2012/12/12 09:03:29 $ $Change: 6006354 $
 */
package provision.services.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

import provision.util.ReflectionUtil;
import provision.util.StringUtil;

/**
 * @author Iulian Vlasov
 * @since TurboPrv
 */
public class TurboRepositorySelector implements RepositorySelector {

	private static final String REPOSITORIES_GETTER = "getRepositories";
	private static LoggerRepository defaultRepo = LogManager.getLoggerRepository();
	private static LoggerRepository systemRepo = defaultRepo;
	private Map<ClassLoader, LoggerRepository> repos;

	/**
	 * @param singleton
	 * 
	 */
	private TurboRepositorySelector(boolean singleton) {
		if (!singleton) {
			repos = new HashMap<ClassLoader, LoggerRepository>();
		}
	}

	/**
	 * If the current application was not registered, return the repository
	 * 
	 * @see org.apache.log4j.spi.RepositorySelector#getLoggerRepository()
	 */
	public LoggerRepository getLoggerRepository() {
		if (repos == null) {
			return defaultRepo;
		}
		else {
			LoggerRepository repo = repos.get(getContextKey());
			return (repo == null ? defaultRepo : repo);
		}
	}

	/**
	 * @return the repos
	 */
	public Map<ClassLoader, LoggerRepository> getRepositories() {
		return repos;
	}

	/**
	 * 
	 */
	public static synchronized boolean addRepository(boolean singleton) {
		if (singleton) {
			if (!wasInitialized()) {
				RepositorySelector parentSelector = new TurboRepositorySelector(true);
				defaultRepo = new TurboRepository(new RootLogger(Level.DEBUG), parentSelector);
				setRepositorySelector(parentSelector);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return addRepository();
		}
	}

	static synchronized boolean addRepository() {
		RepositorySelector parentSelector = null;
		Map<ClassLoader, LoggerRepository> repositories = null;
		TurboRepository myRepo = null;
		ClassLoader ctxKey = getContextKey();

		if (!wasInitialized()) {
			parentSelector = new TurboRepositorySelector(false);
			repositories = ((TurboRepositorySelector) parentSelector).getRepositories();

			myRepo = new TurboRepository(new RootLogger(Level.DEBUG), parentSelector);
			defaultRepo = myRepo;

			setRepositorySelector(parentSelector);
		}
		else {
			parentSelector = getParentSelector();
			repositories = getParentRepositories(parentSelector);
		}

		if (repositories != null) {
			if (myRepo == null) {
				myRepo = new TurboRepository(new RootLogger(Level.DEBUG), parentSelector);
			}

			repositories.put(ctxKey, myRepo);
		}

		return true;
	}

	private static void setRepositorySelector(RepositorySelector selector) {
		try {
			LogManager.setRepositorySelector(selector, LogManager.getRootLogger());
		}
		catch (IllegalArgumentException ignore) {
			// instead of relying on a possibly corrupted repository previously created by a
			// different deployment, we will overwrite it
		}
	}

	/**
	 * 
	 */
	public static synchronized boolean removeRepository() {
		ClassLoader ctxKey = getContextKey();
		LoggerRepository repo = null;

		Map<ClassLoader, LoggerRepository> repositories = getParentRepositories();
		if (StringUtil.isEmpty(repositories)) {
			ClassLoader cl = defaultRepo.getClass().getClassLoader();
			if (cl == ctxKey) {
				repo = defaultRepo;
				defaultRepo = systemRepo;
			}
		}
		else {
			if (repositories.containsKey(ctxKey)) {
				repo = repositories.get(ctxKey);
				repositories.remove(ctxKey);
			}
		}

		if (repo != null) {
			System.out.println("Shutting down the logging module: " + LogConfigurator.getContextName(repo));
			repo.shutdown();

			StringBuilder sb = StringUtil.underline(
					"System Repository: " + LogConfigurator.getContextName(defaultRepo), '-');
			sb.append(LoggingHelper.getLoggerDetails(defaultRepo.getRootLogger()));
			System.out.println(sb);

			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<ClassLoader, LoggerRepository> getParentRepositories() {
		if (wasInitialized()) {
			RepositorySelector parentSelector = getParentSelector();
			Map<ClassLoader, LoggerRepository> repositories = getParentRepositories(parentSelector);

			return repositories;
		}
		else {
			return Collections.EMPTY_MAP;
		}
	}

	/**
	 * @return
	 */
	private static ClassLoader getContextKey() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * @return
	 */
	private static boolean wasInitialized() {
		return defaultRepo.getClass().getName().equals(TurboRepository.class.getName());
	}

	/**
	 * @return
	 */
	private static RepositorySelector getParentSelector() {
		try {
			return (RepositorySelector) ReflectionUtil.invoke(defaultRepo, TurboRepository.SELECTOR_GETTER, null);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static Map<ClassLoader, LoggerRepository> getParentRepositories(RepositorySelector selector) {
		if (selector != null) {
			try {
				return (Map<ClassLoader, LoggerRepository>) ReflectionUtil.invoke(selector, REPOSITORIES_GETTER, null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
