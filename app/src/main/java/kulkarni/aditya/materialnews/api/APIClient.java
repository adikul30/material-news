package kulkarni.aditya.materialnews.api;

import kulkarni.aditya.materialnews.BuildConfig;
import kulkarni.aditya.materialnews.model.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by maverick on 3/2/18.
 */

public interface APIClient {

    @GET("top-headlines?pageSize=50&apiKey=" + BuildConfig.NEWS_API_KEY)
    Call<NewsResponse> getTopHeadlines(
            @Query("sources") String sources
    );

    @GET("everything?language=en&sortBy=popularity&pageSize=50&apiKey=" + BuildConfig.NEWS_API_KEY)
    Call<NewsResponse> searchAnything(
            @Query("q") String q
    );

    @GET("top-headlines?apiKey=" + BuildConfig.NEWS_API_KEY)
    Call<NewsResponseData> getHeadlinesByPage(
            @Query("sources") String sources, @Query("page") int page, @Query("pageSize") int pageSize
    );
}
