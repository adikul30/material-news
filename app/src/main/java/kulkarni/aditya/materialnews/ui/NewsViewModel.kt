package kulkarni.aditya.materialnews.ui

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.PagedList
import kulkarni.aditya.materialnews.api.APIClient
import kulkarni.aditya.materialnews.api.RetrofitInstance
import kulkarni.aditya.materialnews.data.NewsRepository
import kulkarni.aditya.materialnews.db.DatabaseRoom
import kulkarni.aditya.materialnews.db.NewsLocalCache
import kulkarni.aditya.materialnews.model.NewsArticle
import kulkarni.aditya.materialnews.model.Pinned
import kulkarni.aditya.materialnews.util.AppExecutor
import java.util.concurrent.Executors

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository
    private val mDb: DatabaseRoom = DatabaseRoom.getsInstance(this.getApplication())
    private val queryLiveData = MutableLiveData<String>()

    init {
        this.repository = NewsRepository(RetrofitInstance.getRetrofitInstance().create(APIClient::class.java), NewsLocalCache(mDb.newsDao(), Executors.newSingleThreadExecutor()))
    }

    private val result = Transformations.map(queryLiveData) { repository.getNews(it) }

    val news: LiveData<PagedList<NewsArticle>> = Transformations.switchMap(result) { it -> it.data }
        get() = field

    val allPinned: LiveData<List<Pinned>>
        get() = mDb.pinnedDao().pinned


    fun truncateNews() {
        AppExecutor.getInstance().diskIO().execute { mDb.newsDao().truncateNews() }
    }

    fun queryNews(queryString: String) {
        queryLiveData.postValue(queryString)
    }

    fun lastQueryValue(): String? = queryLiveData.value
}

/*  fun insertNewsArticle(newsArticle: NewsArticle) {
        AppExecutor.getInstance().diskIO().execute { mDb.newsDao().addNews(newsArticle) }
    }

    val allNewsArticles: LiveData<List<NewsArticle>>
        get() = mDb.newsDao().news

    fun getPaginatedNews(query: String): LiveData<PagedList<NewsArticle>> {
        return repository.getNews(query)
    }*/