package com.atom.dashboardandroid.Room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.atom.dashboardandroid.Room.Entities.Task;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface DaoTask {
    @Insert
    void insert(Task task);
    @Delete
    void delete(Task task);
    @Update
    void update(Task task);
    @Query("SELECT * FROM task ORDER BY task.date ASC")
    Flowable<List<Task>> getAll();
    @Query("SELECT * FROM task WHERE task.state ='COMPLETED' ORDER BY task.date ASC")
    Flowable<List<Task>> getAllCompleted();
    @Query("SELECT * FROM task WHERE task.state = 'UNCOMPLETED' ORDER BY task.date ASC")
    Flowable<List<Task>> getAllUncompleted();
}
