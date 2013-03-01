package com.example.ddait;

import com.ddai.lib.threadpool.DDAIThreadPool;
import com.ddai.lib.threadpool.IDThreadPool;
import com.ddai.lib.threadpool.IPriorityTask;


public class Test {

	IDThreadPool tp1;
	IDThreadPool tp2;

	public Test() {
		int cpuNumber = Runtime.getRuntime().availableProcessors();
		tp1 = DDAIThreadPool.newFixedThreadPool(1);
		tp2 =  DDAIThreadPool.newFixedThreadPool(1, true);

	}

	public void t1() {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			SubRunnable sub = new SubRunnable("A" + i);
			try {
				tp1.execute("",sub);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void t2() {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			SubRunnable sub = new SubRunnable("B" + i);
			try {
				tp2.execute("",sub, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class SubRunnable implements IPriorityTask {

		String name;

		public SubRunnable(String _name) {
			name = _name;
		}

		@Override
		public void run() {
			System.out.println("" + this.name + " = Start");
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("" + this.name + " = End");
		}

		@Override
		public String getFlag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onRepeatPut(IPriorityTask newTask) {
			// TODO Auto-generated method stub
			
		}
	}
}
