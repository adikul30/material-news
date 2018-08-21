package kulkarni.aditya.materialnews.db

import android.arch.paging.DataSource
import kulkarni.aditya.materialnews.model.NewsArticle
import java.util.concurrent.Executor

class NewsLocalCache(private val newsDao: NewsDao, private val ioExecutor: Executor) {

    fun getNews(): DataSource.Factory<Int, NewsArticle> {
        return newsDao.paginatedNews
    }

    fun insert(newsArticleList: List<NewsArticle>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            newsDao.addNewsByList(newsArticleList)
        }
        insertFinished()
    }
}
