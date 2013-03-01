package com.ddai.lib.commandqueue;

public class Command implements Comparable<Command> {
	public MockRunnable runnable;
	public String action;
	int priority = DDAIQueue.PRIORITY_MEDIUM;
	long time = System.currentTimeMillis();

	public int retryTime = 0;
	private boolean commandSortByLatest = true;

//	public Command(MockRunnable runnable, String action, int priority) {
//		this.runnable = runnable;
//		this.action = action;
//		this.priority = priority;
//	}

	public Command(MockRunnable runnable, String action, int priority,
			boolean commandSortByLatest) {
		this.runnable = runnable;
		this.action = action;
		this.priority = priority;
		this.commandSortByLatest = commandSortByLatest;
	}

	@Override
	public boolean equals(Object o) {
		return runnable.getFlag().equals(((Command) o).runnable.getFlag());
	}

	@Override
	public int hashCode() {
		return runnable.getFlag().hashCode();
	}

	@Override
	public int compareTo(Command another) {
		int result = another.priority - priority;
		if (result == 0) {
			result = (int) (commandSortByLatest ? (another.time - time)
					: (time - another.time));
		}
		return result;
	}

}
