package kulkarni.aditya.materialnews.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.NewsAdapter;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.NewsResponse;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.network.APIClient;
import kulkarni.aditya.materialnews.network.RetrofitInstance;
import kulkarni.aditya.materialnews.util.Constants;
import kulkarni.aditya.materialnews.util.UtilityMethods;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;
import kulkarni.aditya.materialnews.viewmodels.SourcesViewModel;
import retrofit2.Call;
import retrofit2.Callback;

public class UnreadFragment extends Fragment {

    String TAG = getClass().getSimpleName();

    ProgressBar progressBar;
    NewsAdapter newsAdapter;
    List<Sources> sourceArrayList;
    List<NewsArticle> tempList;
    StringBuilder sourcesString = new StringBuilder();
    View rootView;
    int rowCount;
    RelativeLayout rootLayout;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;
    RecyclerView recyclerView;
    Context mContext;
    LottieAnimationView animationView;
    DatabaseRoom mDb;
    NewsViewModel newsViewModel;
    SourcesViewModel sourcesViewModel;

    public UnreadFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sourcesViewModel = ViewModelProviders.of(this).get(SourcesViewModel.class);
        sourcesViewModel.getAllSources().observe(this, new Observer<List<Sources>>() {
            @Override
            public void onChanged(@Nullable List<Sources> sources) {
                for (Sources item : sources) {
                    sourcesString.append(item.getSource());
                    sourcesString.append(",");
                }
                checkConditions();
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_unread, container, false);
            mContext = getActivity();
            sourceArrayList = new ArrayList<>();
            mDb = DatabaseRoom.getsInstance(getActivity());
            progressBar = rootView.findViewById(R.id.progressBar);
            rootLayout = rootView.findViewById(R.id.unreadFragment);
            recyclerView = rootView.findViewById(R.id.unread_recycler_view);
            animationView = rootView.findViewById(R.id.lottie_animation_view);

            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    rowCount = mDb.newsDao().getNewsCount();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            newsAdapter = new NewsAdapter(getActivity(), Constants.UNREAD, animationView);
            recyclerView.setAdapter(new ScaleInAnimationAdapter(newsAdapter));

            connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            activeNetwork = connectivityManager.getActiveNetworkInfo();

            newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
            newsViewModel.getAllNewsArticles().observe(this, new Observer<List<NewsArticle>>() {
                @Override
                public void onChanged(@Nullable List<NewsArticle> newsArticles) {
                    newsAdapter.setList(newsArticles);
                }
            });
        }

        return rootView;
    }

    private void fetchNews() {

        progressBar.setVisibility(View.VISIBLE);

        APIClient apiClient = RetrofitInstance.getRetrofitInstance().create(APIClient.class);
        Log.d(TAG,sourcesString.toString());
        Call<NewsResponse> call = apiClient.getTopHeadlines(sourcesString.toString());

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {

                NewsResponse newsResponse = response.body();
                tempList = new ArrayList<>();
                if (newsResponse != null) {
                    tempList = newsResponse.getNewsArticleList();

                    if (tempList.size() != 0) {
                        for (NewsArticle news : tempList) {
                            newsViewModel.insertNewsArticle(news);
                        }
                    }
                }
                else {
                    Toasty.error(mContext, "Something went wrong. Try back later", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Toasty.error(mContext, "Something went wrong. Try back later", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkConditions(){
        if (rowCount == 0 && !UtilityMethods.checkNet(Objects.requireNonNull(getActivity()))) {
            Snackbar snackbar = Snackbar.make(rootLayout, "You are offline !", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (connectivityManager.getActiveNetworkInfo() != null) {
                                fetchNews();
                            }
                        }
                    });
            snackbar.show();
        } else if (rowCount != 0 && !UtilityMethods.checkNet(Objects.requireNonNull(getActivity()))) {
            Toasty.error(mContext, "You are offline !", Toast.LENGTH_SHORT).show();
        } else if (UtilityMethods.checkNet(getActivity())) {
            fetchNews();
        }
    }
}