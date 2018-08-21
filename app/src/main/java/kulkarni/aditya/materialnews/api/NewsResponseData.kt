package kulkarni.aditya.materialnews.api

import com.google.gson.annotations.SerializedName
import kulkarni.aditya.materialnews.model.NewsArticle

data class NewsResponseData(
        val status: String,
        val totalResults: Int,
        @SerializedName("articles")
        val newsArticleList: List<NewsArticle> = emptyList()
)