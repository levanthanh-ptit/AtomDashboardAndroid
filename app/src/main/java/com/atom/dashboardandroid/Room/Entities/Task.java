package com.atom.dashboardandroid.Room.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "task")
public class Task implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String content;
    public Boolean complete;

    public Task(@NonNull String title, @NonNull String content, Boolean complete) {
        this.title = title;
        this.content = content;
        this.complete = complete;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Boolean getComplete() {
        return complete;
    }
}
