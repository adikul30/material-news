package kulkarni.aditya.materialnews.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.NewsAdapter;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.network.APIClient;
import kulkarni.aditya.materialnews.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchResults extends AppCompatActivity {
    String TAG = getClass().getSimpleName();

    ArrayList<NewsArticle> searchList;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    ActionBar actionBar;
    LinearLayout noMessagesLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView =  findViewById(R.id.search_recycler_view);
        noMessagesLayout = findViewById(R.id.no_messages_error_layout);
        progressBar = findViewById(R.id.search_progress_bar);
        searchList = new ArrayList<>();
        newsAdapter = new NewsAdapter(SearchResults.this, 1, searchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ScaleInAnimationAdapter(newsAdapter));
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            progressBar.setVisibility(View.VISIBLE);
            String query = intent.getStringExtra(SearchManager.QUERY);
            actionBar.setTitle(query);
            APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);
            Call<NewsResponse> call = apiClient.searchAnything(query);

            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    searchList = new ArrayList<>();
                    NewsResponse newsResponse = response.body();
                    searchList.addAll(newsResponse.getNewsArticleList());
                    Log.d(TAG,searchList.toString());
                    if(searchList == null || searchList.size() == 0){
                        progressBar.setVisibility(View.GONE);
                        noMessagesLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        noMessagesLayout.setVisibility(View.GONE);
                        newsAdapter = new NewsAdapter(SearchResults.this, 1, searchList);
                        recyclerView.setAdapter(new ScaleInAnimationAdapter(newsAdapter));
                        newsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    Log.d("failure", t.toString()+"");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
