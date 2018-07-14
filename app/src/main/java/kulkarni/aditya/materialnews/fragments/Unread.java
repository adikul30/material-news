package kulkarni.aditya.materialnews.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.NewsAdapter;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.network.APIClient;
import kulkarni.aditya.materialnews.network.RetrofitInstance;
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
    View rootView;
    RelativeLayout rootLayout;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    RecyclerView recyclerView;
    Context mContext;
    LottieAnimationView animationView;

    public Unread() {


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_unread, container, false);
            mContext = getActivity();
            newsSQLite = new NewsSQLite(getActivity());
            sourceArrayList = new ArrayList<>();

            progressBar = rootView.findViewById(R.id.progressBar);
            rootLayout = rootView.findViewById(R.id.unreadFragment);
            recyclerView = rootView.findViewById(R.id.unread_recycler_view);
            animationView = rootView.findViewById(R.id.lottie_animation_view);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            activeNetwork = connectivityManager.getActiveNetworkInfo();
            new getSourcesFromDb().execute();
        }

        return rootView;
    }

    private void gettingNews() {

//        newsSQLite.dropNewsTable();    //Dropping and adding

        progressBar.setVisibility(View.VISIBLE);

        //Retrofit
        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);

        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {

                NewsResponse newsResponse = response.body();
                newsSQLite.addAllNews(newsResponse.getNewsArticleList());
                progressBar.setVisibility(View.GONE);
                new getNewsFromDb().execute();
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Log.d("failure", t.toString() + "");
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