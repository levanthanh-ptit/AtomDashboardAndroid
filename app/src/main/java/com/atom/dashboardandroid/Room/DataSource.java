package com.atom.dashboardandroid.Room;

import androidx.lifecycle.LiveData;

import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.List;

import io.reactivex.Flowable;


public interface DataSource<T extends Task> {
    void insert(T t);

    void update(T t);

    void delete(T t);

    Flowable<List<T>> getAll();

    Flowable<List<T>> getAllCompleted();

    Flowable<List<Task>> getAllUncompleted();
}
