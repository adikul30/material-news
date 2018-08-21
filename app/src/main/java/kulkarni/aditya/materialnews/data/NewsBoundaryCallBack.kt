package kulkarni.aditya.materialnews.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.util.Log
import kulkarni.aditya.materialnews.api.APIClient
import kulkarni.aditya.materialnews.api.getNews
import kulkarni.aditya.materialnews.db.NewsLocalCache
import kulkarni.aditya.materialnews.model.NewsArticle

class NewsBoundaryCallBack(
        private val query: String,
        private val apiClient: APIClient,
        private val cache: NewsLocalCache
) : PagedList.BoundaryCallback<NewsArticle>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 5
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false


    override fun onZeroItemsLoaded() {
        Log.d("RepoBoundaryCallback", "onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: NewsArticle) {
        Log.d("RepoBoundaryCallback", "onItemAtEndLoaded")
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        getNews(apiClient, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            isRequestInProgress = false
        })
    }
}