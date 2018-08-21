package kulkarni.aditya.materialnews.db;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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

    /**
     * Adds all news articles
     * @param newsArticle list object
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNewsByList(List<NewsArticle> newsArticle);

    @Query("SELECT * FROM newsarticles ORDER BY publishedAt DESC")
    DataSource.Factory<Integer,NewsArticle> getPaginatedNews();

}
