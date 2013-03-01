package com.ddai.lib.threadpool;

public class AbstractCommand implements Comparable<AbstractCommand> {

	private boolean commandSortByLatest = true;
	int priority = IPriorityTask.PRIORITY_MEDIUM;
	long time;

	public AbstractCommand(boolean _commandSortByLatest) {
		commandSortByLatest = _commandSortByLatest;
		time = System.currentTimeMillis();
	}

	public AbstractCommand(boolean _commandSortByLatest, int priority) {
		this(_commandSortByLatest);
		this.priority = priority;
	}

	@Override
	public int compareTo(AbstractCommand another) {
		int result = another.priority - priority;
		if (result == 0) {
			result = (int) (commandSortByLatest ? (another.time - time)
					: (time - another.time));
		}
		return result;
	}

}