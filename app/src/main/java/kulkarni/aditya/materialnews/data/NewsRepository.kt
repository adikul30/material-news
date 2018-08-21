package kulkarni.aditya.materialnews.data

import android.arch.paging.LivePagedListBuilder
import android.util.Log
import kulkarni.aditya.materialnews.api.APIClient
import kulkarni.aditya.materialnews.db.NewsLocalCache
import kulkarni.aditya.materialnews.model.NewsResult

class NewsRepository(
        private val service: APIClient,
        private val cache: NewsLocalCache
) {

    fun getNews(query: String): NewsResult {
        Log.d("NewsRepository", "New query: $query")

        val dataSourceFactory = cache.getNews()

        val boundaryCallback = NewsBoundaryCallBack(query, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        return NewsResult(data,networkErrors)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
