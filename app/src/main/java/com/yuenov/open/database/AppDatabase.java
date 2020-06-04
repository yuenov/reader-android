package com.yuenov.open.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;

import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.yuenov.open.application.MyApplication;
import com.yuenov.open.database.dao.BookChapterDao;
import com.yuenov.open.database.dao.BookShelfDao;
import com.yuenov.open.database.dao.CacheDao;
import com.yuenov.open.database.dao.ReadHistoryDao;
import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.database.tb.TbCache;
import com.yuenov.open.database.tb.TbReadHistory;

@Database(entities = {TbBookChapter.class, TbReadHistory.class, TbBookShelf.class, TbCache.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract BookChapterDao ChapterDao();
    public abstract ReadHistoryDao ReadHistoryDao();
    public abstract BookShelfDao BookShelfDao();
    public abstract CacheDao CacheDao();

    public static AppDatabase getInstance() {
        synchronized (AppDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(MyApplication.getAppContext(), AppDatabase.class, "BookInfo.db")
                        .setJournalMode(JournalMode.TRUNCATE)
                        .allowMainThreadQueries()
//                        .addMigrations(mirgration_0_1)
                        .build();
            }
            return INSTANCE;
        }
    }

    public static final Migration mirgration_0_1 = new Migration(0, 1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
