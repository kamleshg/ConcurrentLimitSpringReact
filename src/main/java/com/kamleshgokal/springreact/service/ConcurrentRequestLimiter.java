package com.kamleshgokal.springreact.service;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Data structure to limit access to a resource on per user basis. Number of
 * concurrent requests allowed per user can be configured. Data is cleaned up
 * every 10 minutes and data related to user will be removed if it is older than
 * 30 minutes and all the requests by that user are completed
 * 
 */
public class ConcurrentRequestLimiter {
	private static final Logger logger = Logger.getLogger(ConcurrentRequestLimiter.class);
	private ConcurrentHashMap<String, Semaphore> requestSemaphore;
	private ConcurrentHashMap<String, Long> timeMap;
	private ReentrantReadWriteLock accessLock = new ReentrantReadWriteLock();
	private TimeBasedEvictor evictor;
	private final static long DEFAULT_EVICTION_TIME = 10;
	private final static TimeUnit DEFAULT_EVICTION_DELAY_UNIT = TimeUnit.MINUTES;
	private final static long DEFAULT_EXPIRY_TIME = 30;
	private final TimeUnit DEFAULT_EXPIRY_TIME_UNIT = TimeUnit.MINUTES;
	private final static long DEFAULT_ACQUIRE_TIMEOUT = 60;
	private final static TimeUnit DEFAULT_ACQUIRE_TIMEUNIT = TimeUnit.SECONDS;
	private long evictionDelay;
	private TimeUnit evictionDealyUnit;
	private long initialDelay;
	private long expiryTime ;
	private TimeUnit expiryTimeUnit;
	private long acquireTimeOut ;
	private TimeUnit acquireTimeOutTimeUnit;

	private int concurrentRequestLimit ;

	private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	public ConcurrentRequestLimiter(int concurrentRequestLimit, long acquireTimeOut,
                                    TimeUnit acquireTimeOutTimeUnit) {
		this.concurrentRequestLimit = concurrentRequestLimit;
		this.acquireTimeOut = acquireTimeOut;
		this.acquireTimeOutTimeUnit = acquireTimeOutTimeUnit;
		this.evictionDelay = DEFAULT_EVICTION_TIME;
		this.evictionDealyUnit = DEFAULT_EVICTION_DELAY_UNIT;
		this.initialDelay = DEFAULT_EVICTION_TIME;
		this.expiryTime = DEFAULT_EXPIRY_TIME;
		this.expiryTimeUnit = DEFAULT_EXPIRY_TIME_UNIT;
		evictor = new TimeBasedEvictor();
		requestSemaphore = new ConcurrentHashMap<>();
		timeMap = new ConcurrentHashMap<>();
		timer.scheduleWithFixedDelay(evictor, initialDelay, evictionDelay, evictionDealyUnit);
	}

	/**
	 * Acquires lock for a user. Will be blocked for acquire time out time to
	 * acquire lock if not available immediately. Will throw exception after timeout
	 * 
	 * @param key
	 */
	public void acquire(String key) throws Exception {
		accessLock.readLock().lock();
		try {
			Semaphore userSemaphore = requestSemaphore.get(key);
			// this if block is to avoid unneccessary creation of semaphore object.
			// putifabsent though atomic first creates an object even if present and
			// this will put unneccessary load on GC
			if (userSemaphore == null) {
				requestSemaphore.putIfAbsent(key, new Semaphore(concurrentRequestLimit));
			}
			Semaphore semaphore = requestSemaphore.get(key);
			logger.trace("Available permits for "+key+" is "+semaphore.availablePermits());
			if (!semaphore.tryAcquire(acquireTimeOut, acquireTimeOutTimeUnit)) {
				logger.error("Acquiring lock timed out for: " + key);
				throw new Exception(
						"Acquiring lock timed out for: " + key);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			accessLock.readLock().unlock();
		}
	}

	public void release(String userId) {
		accessLock.readLock().lock();
		try {
			requestSemaphore.get(userId).release();
			timeMap.put(userId, System.currentTimeMillis());
		} finally {
			accessLock.readLock().unlock();
		}
	}

	public void close() {
		requestSemaphore = null;
		timeMap = null;
		timer.shutdown();
		// wait for 5 seconds after instantiating the shutdown
		for (int i = 0; !timer.isShutdown(); i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (i > 5) {

				break;

			}
		}
	}

	private class TimeBasedEvictor implements Runnable {

		@Override
		public void run() {
			if (requestSemaphore.size() <= 0) {
				return;
			}

			List<String> markForRemoval = new ArrayList<String>();
			accessLock.readLock().lock();
			try {
				Set<String> userIds = requestSemaphore.keySet();

				for (String user : userIds) {
					long timeLastAccessed = timeMap.get(user);
					long interval = System.currentTimeMillis() - timeLastAccessed;
					long elapsedTime = TimeUnit.NANOSECONDS.convert(interval, expiryTimeUnit);
					if (requestSemaphore.get(user).availablePermits() == concurrentRequestLimit
							&& elapsedTime > expiryTime) {
						markForRemoval.add(user);
					}
				}
			} finally {
				accessLock.readLock().unlock();
			}
			
			// acquire write lock only if there are items to cleanup.
			if (markForRemoval.size() > 0) {
				accessLock.writeLock().lock();
				try {
					for (String user : markForRemoval) {
						long timeLastAccessed = timeMap.get(user);
						long interval = System.currentTimeMillis() - timeLastAccessed;
						long elapsedTime = TimeUnit.NANOSECONDS.convert(interval, expiryTimeUnit);
						if (requestSemaphore.get(user).availablePermits() == concurrentRequestLimit
								&& elapsedTime > expiryTime) {
							timeMap.remove(user);
							requestSemaphore.remove(user);
						}
					}
				} finally {
					accessLock.writeLock().unlock();
				}
			}
		}

	}

}
