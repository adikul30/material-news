package kulkarni.aditya.materialnews.api

import android.util.Log
import kulkarni.aditya.materialnews.model.NewsArticle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "NewsService"

fun getNews(
        service: APIClient,
        query: String,
        page: Int,
        itemsPerPage: Int,
        onSuccess: (repos: List<NewsArticle>) -> Unit,
        onError: (error: String) -> Unit) {
    Log.d(TAG, "query: $query, page: $page, itemsPerPage: $itemsPerPage")


    service.getHeadlinesByPage(query, page, itemsPerPage).enqueue(
            object : Callback<NewsResponseData> {
                override fun onFailure(call: Call<NewsResponseData>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<NewsResponseData>?,
                        response: Response<NewsResponseData>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val repos = response.body()?.newsArticleList ?: emptyList()
                        onSuccess(repos)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}