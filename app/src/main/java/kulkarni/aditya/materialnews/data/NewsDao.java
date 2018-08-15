package kulkarni.aditya.materialnews.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import kulkarni.aditya.materialnews.model.NewsArticle;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNews(NewsArticle newsArticle);

    @Query("SELECT * FROM newsarticles ORDER BY publishedAt DESC")
    LiveData<List<NewsArticle>> getNews();

    @Query("SELECT * FROM newsarticles ORDER BY publishedAt DESC LIMIT :limit")
    List<NewsArticle> getTopNRows(int limit);

    @Query("SELECT COUNT(*) FROM newsarticles")
    int getNewsCount();

    @Query("DELETE FROM newsarticles")
    void truncateNews();

    @Query("SELECT * FROM newsarticles WHERE isPinned = 1 ORDER BY publishedAt DESC")
    LiveData<List<NewsArticle>> getPinned();

    @Query("UPDATE newsarticles SET isPinned = 1 WHERE title = :title")
    void pinArticle(String title);

    @Query("UPDATE newsarticles SET isPinned = 0 WHERE title = :title")
    void unpinArticle(String title);

}
