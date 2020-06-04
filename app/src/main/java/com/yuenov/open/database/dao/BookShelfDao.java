package com.yuenov.open.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yuenov.open.database.AppDatabase;
import com.yuenov.open.database.tb.TbBookShelf;
import com.yuenov.open.utils.UtilityException;

import java.util.List;

@Dao
public abstract class BookShelfDao {

    @Update
    public abstract void update(TbBookShelf... entities);

    @Insert
    public abstract void insert(TbBookShelf... entities);

    @Delete
    public abstract void delete(TbBookShelf... entities);

    @Query("select * from TbBookShelf order by addTime desc")
    public abstract List<TbBookShelf> getAllList();

    @Query("delete from TbBookShelf where bookId = :bookId")
    public abstract void delete(int bookId);

    @Query("select * from TbBookShelf where bookId = :bookId")
    public abstract TbBookShelf getEntity(int bookId);

    @Query("update TbBookShelf set hasUpdate = :hasUpdate , addTime = :updateTime where bookId = :bookId")
    public abstract void updateHasUpdate(int bookId, boolean hasUpdate, long updateTime);

    public boolean exists(int bookId) {
        TbBookShelf tbBookShelf = getEntity(bookId);
        return tbBookShelf != null;
    }

    public void addOrUpdate(TbBookShelf tbBookShelf) {
        if (tbBookShelf == null || tbBookShelf.bookId < 1)
            return;

        try {
            TbBookShelf existsTbBookShelf = getEntity(tbBookShelf.bookId);
            if (existsTbBookShelf != null) {
                tbBookShelf.id = existsTbBookShelf.id;
                update(tbBookShelf);
            } else {
                insert(tbBookShelf);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    /**
     * 删除书架上的书，同时删除目录中的书
     *
     * @param bookId
     */
    public void deleteByBookId(int bookId) {
        try {
            delete(bookId);

            AppDatabase.getInstance().ChapterDao().delete(bookId);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}