package com.yuenov.open.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yuenov.open.database.tb.TbReadHistory;
import com.yuenov.open.utils.UtilityException;

import java.util.List;

@Dao
public abstract class ReadHistoryDao {

    @Update
    public abstract void update(TbReadHistory... entities);

    @Insert
    public abstract void insert(TbReadHistory... entities);

    @Delete
    public abstract void delete(TbReadHistory... entities);

    /**
     * 获取某条阅读记录
     */
    @Query("select * from TbReadHistory where bookId = :bookId")
    public abstract TbReadHistory getEntity(int bookId);

    /**
     * 获取所有阅读记录
     */
    @Query("select * from TbReadHistory order by lastReadTime desc")
    public abstract List<TbReadHistory> getAllList();

    @Query("delete from TbReadHistory")
    public abstract void clear();

    @Query("delete from TbReadHistory where bookId = :bookId")
    public abstract void deleteByBookId(int bookId);

    @Query("update TbReadHistory set addBookShelf = :stat where bookId = :bookId")
    public abstract void resetAddBookShelfStat(int bookId, boolean stat);

    public boolean exists(int bookId) {
        TbReadHistory tbBookShelf = getEntity(bookId);
        return tbBookShelf != null;
    }

    /**
     * 是否阅读过，有章节id即表示阅读过
     *
     * @param bookId
     * @return
     */
    public boolean existsRealRead(int bookId) {
        TbReadHistory tbBookShelf = getEntity(bookId);
        return tbBookShelf != null && tbBookShelf.chapterId > 0;
    }

    public void addOrUpdateByPreview(TbReadHistory entity) {
        try {
            TbReadHistory existsEntity = getEntity(entity.bookId);
            if (existsEntity == null) {
                insert(entity);
            } else {
                entity.id = existsEntity.id;

                if (entity.chapterId < 1) {
                    entity.chapterId = existsEntity.chapterId;
                    entity.page = existsEntity.page;
                }
                update(entity);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    public void addOrUpdateByReadDetail(TbReadHistory entity) {
        try {
            TbReadHistory existsEntity = getEntity(entity.bookId);
            if (existsEntity == null) {
                insert(entity);
            } else {
                entity.id = existsEntity.id;
                update(entity);
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}