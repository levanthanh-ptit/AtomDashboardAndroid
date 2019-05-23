package com.atom.dashboardandroid.Room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.atom.dashboardandroid.Room.DAO.DaoTask;
import com.atom.dashboardandroid.Room.Entities.Task;

@Database(entities = {Task.class}, version = 1)
public abstract class AtomDatabase extends RoomDatabase {
    private static AtomDatabase instance = null;
    private static String DB_NAME = "ATOMDB";

    public abstract DaoTask daoTask();

    public static AtomDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AtomDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AtomDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration().addCallback(seedingCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }

    private static RoomDatabase.Callback seedingCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new SeedingDBAsyncTask(instance).execute();
        }
    };

    private static class SeedingDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private DaoTask daoTask;

        SeedingDBAsyncTask(AtomDatabase db) {
            daoTask = db.daoTask();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 1; i <= 10; i++) {
                daoTask.insert(new Task("Task " + i, "Content: This is task " + i + "'s content", i%2==0));
            }
            return null;
        }
    }
}
