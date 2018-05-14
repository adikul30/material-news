package kulkarni.aditya.materialnews.Util;

import kulkarni.aditya.materialnews.BuildConfig;
import kulkarni.aditya.materialnews.Model.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by maverick on 3/2/18.
 */

public interface APIClient {

//    String apiKey = BuildConfig.ApiKey;

    @GET("top-headlines?apiKey=" + BuildConfig.NEWS_API_KEY)
    Call<NewsResponse> getTopHeadlines(
            @Query("sources") String sources
    );

    @GET("everything?language=en&sortBy=popularity&pageSize=50&apiKey=" + BuildConfig.NEWS_API_KEY)
    Call<NewsResponse> searchAnything(
            @Query("q") String q
    );
}
