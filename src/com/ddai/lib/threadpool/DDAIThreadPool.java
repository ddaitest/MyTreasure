package com.ddai.lib.threadpool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DDAIThreadPool implements IDThreadPool {
	ThreadPoolExecutor me;
	CommandFactory mcf;
	PriorityBlockingQueue<Runnable> q;

	public static IDThreadPool newFixedThreadPool(int coreCount,
			boolean sortByLatest) {
		return new DDAIThreadPool(coreCount, coreCount, 0, sortByLatest);
	}

	public static IDThreadPool newCachedThreadPool(boolean sortByLatest) {
		return new DDAIThreadPool(0, Integer.MAX_VALUE, 10000, sortByLatest);
	}

	public static IDThreadPool newFixedThreadPool(int coreCount) {
		return new DDAIThreadPool(coreCount, coreCount, 0);
	}

	public static IDThreadPool newCachedThreadPool() {
		return new DDAIThreadPool(0, Integer.MAX_VALUE, 10000);
	}

	public DDAIThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, boolean sortByLatest) {
		q = new PriorityBlockingQueue<Runnable>();
		me = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS, q,
				new PriorityThreadFactory("thread-pool", 10));
		mcf = new CommandFactory(sortByLatest);
	}

	public DDAIThreadPool(int corePoolSize, int maximumPoolSize,
			long keepAliveTime) {
		q = new PriorityBlockingQueue<Runnable>();
		me = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS, q,
				new PriorityThreadFactory("thread-pool", 10));
		mcf = new CommandFactory();
	}

	ITaskHandler th = new ITaskHandler() {

		@Override
		public void onFinish(String flag) {
			synchronized (tm) {
				if (tm.containsKey(flag)) {
					tm.remove(flag);
				}
			}
		}
	};

	public void execute(String category, IPriorityTask runnable) {
		if (runnable != null) {
			me.execute(mcf.getTask(category, runnable, th));
		}
	}

	public void execute(String category, IPriorityTask runnable, int priority) {
		if (runnable != null) {
			me.execute(mcf.getTask(category, runnable, priority, th));
		}
	}

	@Override
	public boolean isTerminated() {
		return me.isTerminated();
	}

	@Override
	public boolean isShutdown() {
		return me.isShutdown();
	}

	@Override
	public void shutdownNow() {
		me.shutdownNow();
	}

	HashMap<String, IPriorityTask> tm = new HashMap<String, IPriorityTask>();

	public void put(String category, IPriorityTask runnable) {
		String key = runnable.getFlag();
		synchronized (tm) {
			if (tm.containsKey(key)) {
				tm.get(key).onRepeatPut(runnable);
			} else {
				tm.put(runnable.getFlag(), runnable);
				execute(category, runnable);
			}
		}
	}

	@Override
	public void stopQueue(String category) {
		synchronized (q) {
			Iterator<Runnable> keys = q.iterator();
			Runnable cmd;
			ArrayList<Runnable> buffer = new ArrayList<Runnable>();
			while (keys.hasNext()) {
				cmd = keys.next();
				if (((PriorityTask) cmd).category.equals(category)) {
					buffer.add(cmd);
				}
			}
			q.removeAll(buffer);
		}
	}
}
