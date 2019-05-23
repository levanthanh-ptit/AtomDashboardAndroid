package com.atom.dashboardandroid.TaskList.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.atom.dashboardandroid.Room.Entities.Task;
import com.atom.dashboardandroid.Room.Repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<Task>> mAllTasks;
    public TaskViewModel(@NonNull Application application) {
        super(application);
        this.taskRepository = new TaskRepository(application);
        mAllTasks = taskRepository.getAll();
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public LiveData<List<Task>> getAll() {
        return mAllTasks;
    }

}