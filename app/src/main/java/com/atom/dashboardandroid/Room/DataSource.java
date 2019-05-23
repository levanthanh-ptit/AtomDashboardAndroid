package com.atom.dashboardandroid.Room;

import androidx.lifecycle.LiveData;

import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.List;


public interface DataSource<T extends Task> {
    void insert(T t);

    void update(T t);

    void delete(T t);

    LiveData<List<T>> getAll();
}
