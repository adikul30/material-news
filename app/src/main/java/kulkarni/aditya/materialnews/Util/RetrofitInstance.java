package kulkarni.aditya.materialnews.Util;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by maverick on 3/2/18.
 */

public class RetrofitInstance {
    public static String BASE_URL="https://newsapi.org/v2/";
    public static Retrofit retrofit=null;

    public static Retrofit getRetrofitInstance(){
        if(retrofit==null){

//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().build();

            retrofit= new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
