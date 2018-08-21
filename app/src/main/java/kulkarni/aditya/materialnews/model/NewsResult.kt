package kulkarni.aditya.materialnews.model

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList

data class NewsResult (
    val data: LiveData<PagedList<NewsArticle>>,
    val networkErrors: LiveData<String>
)