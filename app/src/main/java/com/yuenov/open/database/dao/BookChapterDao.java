package com.yuenov.open.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;
import androidx.room.Update;

import com.yuenov.open.database.tb.TbBookChapter;
import com.yuenov.open.model.standard.BookMenuItemInfo;
import com.yuenov.open.model.standard.DownloadBookContentItemInfo;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Dao
public abstract class BookChapterDao {

    @Update
    public abstract void update(TbBookChapter... entities);

    @Insert
    public abstract void insert(TbBookChapter... entities);

    @Delete
    public abstract void delete(TbBookChapter... entities);

    @Query("select * from TbBookChapter")
    public abstract TbBookChapter getAll();

    /**
     * 获取章节信息，只有chapterId一个字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select id,bookId,chapterId from TbBookChapter where bookId = :bookId order by chapterId asc")
    public abstract List<TbBookChapter> getChapterList(int bookId);

    /**
     * 获取章节信息，只有chapterId一个字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select id,bookId,chapterId,chapterName from TbBookChapter where bookId = :bookId order by chapterId asc")
    public abstract List<TbBookChapter> getAllColumnChapterList(int bookId);

    @Query("select chapterId from TbBookChapter where bookid = :bookId")
    public abstract List<Long> getChapterIds(int bookId);

    @Query("select * from TbBookChapter where bookId = :bookId order by chapterId asc")
    public abstract List<TbBookChapter> getListByBookIdOrderByAsc(int bookId);

    /**
     * content字段有内容返回1，无内容返回null
     * @param bookId
     * @return
     */
    @Query("select id,bookId,ChapterId,ChapterName,(case when content is null then null else '0' end) as content from TbBookChapter where bookId = :bookId order by chapterId asc")
    public abstract List<TbBookChapter> getChapterListByBookIdOrderByAsc(int bookId);

    @Query("select * from TbBookChapter where bookId = :bookId order by chapterId desc")
    public abstract List<TbBookChapter> getListByBookIdOrderByDesc(int bookId);

    @Query("select * from TbBookChapter where id = :id")
    public abstract TbBookChapter getEntity(int id);

    @Query("select * from TbBookChapter where bookId = :bookId and chapterId = :chapterId")
    public abstract TbBookChapter getEntity(int bookId, long chapterId);

    @Query("delete from TbBookChapter where bookId = :bookId")
    public abstract void delete(int bookId);

    /**
     * 获取上一章
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId < :chapterId order by chapterId desc limit 1 offset 0")
    public abstract TbBookChapter getPreEntity(int bookId, long chapterId);

    /**
     * 获取下一章
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select * from TbBookChapter where bookId = :bookId and chapterId > :chapterId order by chapterId limit 1 offset 0")
    public abstract TbBookChapter getNextEntity(int bookId, long chapterId);

    @Query("select count(*) from TbBookChapter where bookId = :bookId")
    public abstract Integer getCountsByBookId(int bookId);

    /**
     * 获取已下载的最后一章
     *
     * @param bookId
     * @return
     */
    @Query("select max(chapterId) from TbBookChapter where bookid = :bookId and [content] is not null")
    public abstract long getLastDownloadChapterId(int bookId);

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where bookid = :bookId and content is null")
    public abstract List<TbBookChapter> getAllUnDownloadChapterId(int bookId);

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookid = :bookId and chapterId > :chapterId order by chapterId limit :downloadCounts offset 0")
    public abstract List<TbBookChapter> getAfterChapterId(int bookId, long chapterId, int downloadCounts);

    /**
     * 获取该章节之后的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookid = :bookId and chapterId > :chapterId and content is null order by chapterId limit :downloadCounts offset 0")
    public abstract List<TbBookChapter> getUnDownloadAfterChapterId(int bookId, long chapterId, int downloadCounts);

    /**
     * 获取该章节之前的章节
     *
     * @param bookId
     * @param chapterId
     * @param downloadCounts 个数
     * @return
     */
    @Query("select * from TbBookChapter where bookid = :bookId and chapterId < :chapterId order by chapterId desc limit :downloadCounts offset 0")
    public abstract List<TbBookChapter> getBeforeChapterId(int bookId, long chapterId, int downloadCounts);

    /**
     * 获取所有待下载章节id
     *
     * @param bookId
     * @param chapterId
     * @return
     */
    @Query("select chapterId from TbBookChapter where bookid = :bookId and chapterId > :chapterId and [content] is null order by chapterId")
    public abstract List<Long> getAllDownloadChapterId(int bookId, long chapterId);

    /**
     * 获取第一章信息
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where booKId = :bookId order by chapterId asc limit 1 offset 0")
    public abstract TbBookChapter getFirstChapter(int bookId);

    /**
     * 获取最后一章信息
     *
     * @param bookId
     * @return
     */
    @Query("select * from TbBookChapter where booKId = :bookId order by chapterId desc limit 1 offset 0")
    public abstract TbBookChapter getLastChapter(int bookId);

    /**
     * 只有bookId，和chapterId 两个字段
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("select TbBookChapter.id,TbBookChapter.bookId,(max(chapterId)) chapterId from TbBookShelf left join TbBookChapter on TbBookShelf.bookId = TbBookChapter.bookId group by TbBookChapter.bookId")
    public abstract List<TbBookChapter> getShelfUpdateInfo();

    @Transaction
    public void addChapter(List<TbBookChapter> list) {
        if (UtilitySecurity.isEmpty(list))
            return;

        try {
            // 查询出已存在的
            List<Long> lisChapterIds = getChapterIds(list.get(0).bookId);
            HashSet<Long> hsChapterIds = new HashSet<>();
            for (int i = 0; i < lisChapterIds.size(); i++) {
                hsChapterIds.add(lisChapterIds.get(i));
            }

            // 不存在才插入
            for (int i = 0; i < list.size(); i++) {
                if (!hsChapterIds.contains(list.get(i).chapterId))
                    insert(list.get(i));
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Transaction
    public void addChapter(int bookId, List<BookMenuItemInfo> list) {
        if (bookId < 1 || UtilitySecurity.isEmpty(list))
            return;

        try {
            List<TbBookChapter> listTbBookChapter = new ArrayList<>();
            TbBookChapter tbBookChapter;

            for (int i = 0; i < list.size(); i++) {
                tbBookChapter = new TbBookChapter();
                tbBookChapter.bookId = bookId;
                tbBookChapter.chapterId = list.get(i).id;
                tbBookChapter.chapterName = list.get(i).name;
                listTbBookChapter.add(tbBookChapter);
            }

            addChapter(listTbBookChapter);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Transaction
    public void addContent(List<TbBookChapter> list) {
        if (UtilitySecurity.isEmpty(list))
            return;

        try {
            TbBookChapter existsEntity = null;
            for (int i = 0; i < list.size(); i++) {

                // 查询出已存在的
                existsEntity = getEntity(list.get(i).bookId, list.get(i).chapterId);
                if (existsEntity == null) {
                    insert(list.get(i));
                } else {
                    // 已存在 但内容为空 则更新
                    if (UtilitySecurity.isEmpty(existsEntity.content)) {
                        existsEntity.chapterName = list.get(i).chapterName;
                        existsEntity.content = list.get(i).content;
                        update(existsEntity);
                    }
                }
            }
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }

    @Transaction
    public void addContent(int bookId, List<DownloadBookContentItemInfo> list) {
        if (bookId < 1 || UtilitySecurity.isEmpty(list))
            return;

        try {
            List<TbBookChapter> lisAdd = new ArrayList<>();
            TbBookChapter tbBookChapter;
            for (int i = 0; i < list.size(); i++) {
                tbBookChapter = new TbBookChapter();
                tbBookChapter.bookId = bookId;
                tbBookChapter.chapterId = list.get(i).id;
                tbBookChapter.chapterName = list.get(i).name;
                tbBookChapter.content = list.get(i).content;

                lisAdd.add(tbBookChapter);
            }

            addContent(lisAdd);
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }
    }
}