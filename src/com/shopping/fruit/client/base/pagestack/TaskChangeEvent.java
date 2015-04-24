package com.shopping.fruit.client.base.pagestack;

public class TaskChangeEvent {
  static final int TASK_CUR_CHANGE = 0;
  static final int TASK_REMOVE = 1;
  int type = -1;
  Task curTask;
}
