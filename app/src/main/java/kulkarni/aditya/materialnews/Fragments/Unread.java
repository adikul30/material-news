package kulkarni.aditya.materialnews.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.Activities.FilterSources;
import kulkarni.aditya.materialnews.Adapters.NewsAdapter;
import kulkarni.aditya.materialnews.Model.NewsArticle;
import kulkarni.aditya.materialnews.Model.NewsResponse;
import kulkarni.aditya.materialnews.Model.Sources;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.Util.APIClient;
import kulkarni.aditya.materialnews.Util.NewsSQLite;
import kulkarni.aditya.materialnews.Util.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;

public class Unread extends Fragment {

    String TAG = getClass().getSimpleName();

    ProgressBar progressBar;
    NewsAdapter newsAdapter;
    ArrayList<Sources> sourceArrayList;
    ArrayList<NewsArticle> newsArticleArrayList = new ArrayList<>();
    StringBuilder sourcesString = new StringBuilder();
    NewsSQLite newsSQLite;
    RelativeLayout rootLayout;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    RecyclerView recyclerView;
    Context mContext;
    FloatingActionButton fab;
    LottieAnimationView animationView;

    public Unread() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unread, container, false);
        mContext = getActivity();
        newsSQLite = new NewsSQLite(getActivity());
        sourceArrayList = new ArrayList<>();

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        rootLayout = (RelativeLayout) rootView.findViewById(R.id.unreadFragment);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.unread_recycler_view);
        animationView = (LottieAnimationView) rootView.findViewById(R.id.lottie_animation_view);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();

        new getSourcesFromDb().execute();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && fab.isShown()) {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FilterSources.class));
            }
        });

        return rootView;
    }

    private void gettingNews() {

        newsSQLite.dropNewsTable();    //Dropping and adding

        progressBar.setVisibility(View.VISIBLE);

        //Retrofit
        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);

        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, retrofit2.Response<NewsResponse> response) {

                NewsResponse newsResponse = response.body();
                newsSQLite.addAllNews(newsResponse.getNewsArticleList());
                progressBar.setVisibility(View.GONE);
                new getNewsFromDb().execute();
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.d("failure", t.toString()+"");
            }
        });

    }

    public class getNewsFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            newsArticleArrayList = newsSQLite.getAllRows();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute messagesArrayList size=" + newsArticleArrayList.size());
            if (newsArticleArrayList.size() == 0) {

            } else {
                newsAdapter = new NewsAdapter(getActivity(), 2, newsArticleArrayList, animationView);
                recyclerView.setAdapter(new ScaleInAnimationAdapter(newsAdapter));
                newsAdapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public class getSourcesFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            sourceArrayList = newsSQLite.getAllSources();
            for (int i = 0; i < sourceArrayList.size(); i++) {
                sourcesString.append(sourceArrayList.get(i).getSource());
                sourcesString.append(",");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("MessageFragment", "onPostExecute messagesArrayList size=" + newsArticleArrayList.size());

            progressBar.setVisibility(View.GONE);

            if (newsSQLite.getRowCount() == 0 && activeNetwork == null) {
                Snackbar snackbar = Snackbar.make(rootLayout, "YOU ARE OFFLINE", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (connectivityManager.getActiveNetworkInfo() != null) {
                                    gettingNews();
                                }
                            }
                        });
                snackbar.show();
            } else if (newsSQLite.getRowCount() != 0 && connectivityManager.getActiveNetworkInfo() == null) {
                Toasty.error(mContext, "You are OFFLINE !", Toast.LENGTH_SHORT).show();
                new getNewsFromDb().execute();
            } else if (activeNetwork != null) {
                Log.d("TAG", "Before call getting news");
                gettingNews();
            }
        }
    }
}