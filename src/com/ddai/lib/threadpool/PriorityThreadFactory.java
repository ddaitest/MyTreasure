package com.ddai.lib.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

public class PriorityThreadFactory implements ThreadFactory {

	private final String name;
	private final AtomicInteger number = new AtomicInteger();
	private final int priority;

	public PriorityThreadFactory(String name, int priority) {
		this.name = name;
		this.priority = priority;

	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, this.name + ":" + number) {
			@Override
			public void run() {
				Process.setThreadPriority(PriorityThreadFactory.this.priority);
				super.run();
			}
		};
	}

}
