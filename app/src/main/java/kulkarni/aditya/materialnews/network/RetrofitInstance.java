package kulkarni.aditya.materialnews.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kulkarni.aditya.materialnews.model.SourceInfo;
import kulkarni.aditya.materialnews.util.MyDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

            Gson gson =
                    new GsonBuilder()
                            .registerTypeAdapter(SourceInfo.class, new MyDeserializer<SourceInfo>())
                            .create();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            retrofit= new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
