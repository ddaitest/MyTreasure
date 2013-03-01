package com.ddai.lib.commandqueue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.os.Process;

public class DDAIQueue implements Runnable {

	public static final int PRIORITY_BACKGROUND = 1;
	public static final int PRIORITY_LOW = 2;
	public static final int PRIORITY_MEDIUM = 3;
	public static final int PRIORITY_HIGH = 4;
	public static final int PRIORITY_TOP = 5;

	public static final int NETWORK_FAIL_WAIT = 20 * 1000;
	public static final long PUT_COMMAND_RETRY_INTERVAL = 200;

	boolean coreFlag = true;

	private Thread mThread;
	public int sleepTime = 10 * 1000;

	public boolean commandSortByLatest = true;
	private BlockingQueue<Command> mCommands = new PriorityBlockingQueue<Command>();

	public DDAIQueue() {
		mThread = new Thread(this);
		mThread.setName("DDAIQueue");
		mThread.start();
	}

	public synchronized void stopQueue(String action) {
		synchronized (mCommands) {
			Iterator<Command> keys = mCommands.iterator();
			Command cmd;
			ArrayList<Command> buffer = new ArrayList<Command>();
			while (keys.hasNext()) {
				cmd = keys.next();
				if (cmd.action.equals(action)) {
					buffer.add(cmd);
				}
			}
			mCommands.removeAll(buffer);
		}
	}

	private void resetSleepTime() {
		sleepTime = 10 * 1000;
	}

	private int increaseSleepTime() {
		if (sleepTime == 10 * 1000) {
			sleepTime = 30 * 1000;
		} else if (sleepTime == 30 * 1000) {
			sleepTime = 3 * 60 * 1000;
		} else if (sleepTime == 3 * 60 * 1000) {
			sleepTime = 5 * 60 * 1000;
		}
		return sleepTime;
	}

	public void put(String action, int priority, MockRunnable runnable) {
		Iterator<Command> keys = mCommands.iterator();
		Command cmd;
		String newFlag = runnable.getFlag();
		boolean found = false;
		if (runningFlag != null) {
			found = runningFlag.getFlag().equals(newFlag);
			if (found) {
				runningFlag.addListener(runnable.getListener());
			}
		}
		if (!found) {
			while (keys.hasNext()) {
				cmd = keys.next();
				if (cmd.runnable.getFlag().equals(runnable.getFlag())) {
					found = true;
					cmd.runnable.addListener(runnable.getListener());
				}
			}
		}
		if (!found) {
			putCommand(mCommands, action, runnable, priority);
		}
	}

	private void putCommand(BlockingQueue<Command> queue, String action,
			MockRunnable runnable, int priority) {
		int retries = 10;
		Exception e = null;
		while (retries-- > 0) {
			try {
				Command command = new Command(runnable, action, priority,
						commandSortByLatest);
				queue.put(command);
				return;
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				try {
					Thread.sleep(PUT_COMMAND_RETRY_INTERVAL);
				} catch (InterruptedException ne) {
					ne.printStackTrace();
				}
				e = ie;
			}
		}
		throw new Error(e);
	}

	private MockRunnable runningFlag = null;

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
		coreFlag = true;
		while (coreFlag) {
			// String commandDescription = null;
			try {
				final Command command = mCommands.take();
				// if (!connected) {
				// if (command != null) {
				// command.runnable.timeoutCallback();
				// continue;
				// }
				// synchronized (connectionLock) {
				// connectionLock.wait(NETWORK_FAIL_WAIT);
				// continue;
				// }
				// }

				if (command != null) {
					try {
						runningFlag = command.runnable;
						command.runnable.run();
						runningFlag = null;
						resetSleepTime();
					} catch (Exception e) {
						// if (command.retryTime > 0) {
						// command.retryTime -= 1;
						//
						// new Thread() {
						// @Override
						// public void run() {
						// try {
						// sleep(increaseSleepTime());
						// mCommands.put(command);
						// } catch (InterruptedException e) {
						// }
						// }
						//
						// }.start();
						// }

						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
