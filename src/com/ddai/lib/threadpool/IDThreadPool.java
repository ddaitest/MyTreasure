package com.ddai.lib.threadpool;

public interface IDThreadPool {

	void execute(String category, IPriorityTask runnable);

	void execute(String category, IPriorityTask runnable, int priority);

	boolean isTerminated();

	boolean isShutdown();

	void shutdownNow();

	void stopQueue(String tag);

	void put(String category, IPriorityTask runnable);
}
