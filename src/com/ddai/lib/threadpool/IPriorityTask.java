package com.ddai.lib.threadpool;


public interface IPriorityTask {
	public static final int PRIORITY_BACKGROUND = 1;
	public static final int PRIORITY_LOW = 2;
	public static final int PRIORITY_MEDIUM = 3;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_TOP = 5;

	void run();

	String getFlag();

	void onRepeatPut(IPriorityTask newTask);
}
