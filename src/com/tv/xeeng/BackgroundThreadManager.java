package com.tv.xeeng;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundThreadManager {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int KEEP_ALIVE_TIME = 1;
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
	private static final int NUMBER_OF_CORES = Runtime.getRuntime()
			.availableProcessors();

	// ===========================================================
	// Fields
	// ===========================================================
	private static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
	private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
			NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
			KEEP_ALIVE_TIME_UNIT, workQueue);

	// ===========================================================
	// Constructors
	// ===========================================================
	private BackgroundThreadManager() {}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	public static void post(Runnable runnable) {
		threadPool.execute(runnable);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
