package com.atom.dashboardandroid.TaskList.View;

import com.atom.dashboardandroid.Room.Entities.Task;

public interface OnActionListener {
    void OnShowDetail(Task task, int position);
    void OnDeleteTask(Task task, int position);
    void OnEditTask(Task task);
    void OnTaskChange(Task task, int position);
}
