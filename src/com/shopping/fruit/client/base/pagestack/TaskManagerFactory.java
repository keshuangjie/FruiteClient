package com.shopping.fruit.client.base.pagestack;

public final class TaskManagerFactory {
	public static TaskManager getTaskManager() {
		return TaskManagerImpl.getInstance();
	}
}
