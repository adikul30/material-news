package kulkarni.aditya.materialnews.fragments;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private ProgressBar progressBar;
    private NewsAdapter newsAdapter;
    private List<Sources> sourceArrayList;
    private List<NewsArticle> tempList;
    private StringBuilder sourcesString = new StringBuilder();
    private View rootView;
    private int rowCount;
    private CoordinatorLayout rootLayout;
    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;
    private RecyclerView recyclerView;
    private Context mContext;
    private LottieAnimationView animationView;
    private DatabaseRoom mDb;
    private NewsViewModel newsViewModel;
    private SourcesViewModel sourcesViewModel;
    private FloatingActionButton filterFAB;

    public UnreadFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sourcesViewModel = ViewModelProviders.of(this).get(SourcesViewModel.class);
        sourcesViewModel.getAllSources().observe(this, new Observer<List<Sources>>() {
            @Override
            public void onChanged(@Nullable List<Sources> sources) {
                assert sources != null;
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
            rootLayout = rootView.findViewById(R.id.unread_root_layout);
            recyclerView = rootView.findViewById(R.id.unread_recycler_view);
            animationView = rootView.findViewById(R.id.lottie_animation_view);
//            filterFAB = rootView.findViewById(R.id.filter_fab);

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

//            filterFAB.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(getActivity(), FilterSources.class));
//                }
//            });
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
                    Snackbar.make(rootLayout, "Something went wrong. Try back later", Snackbar.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                Snackbar.make(rootLayout, "Something went wrong. Try back later", Snackbar.LENGTH_SHORT).show();
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
            Log.d(TAG,"no net");
//            Snackbar snackbar = Snackbar.make(rootLayout, "You are offline !", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (connectivityManager.getActiveNetworkInfo() != null) {
//                        fetchNews();
//                    }
//                }
//            });
//            snackbar.show();
            Toast.makeText(getActivity(), "You are offline !", Toast.LENGTH_LONG).show();
        } else if (UtilityMethods.checkNet(getActivity())) {
            fetchNews();
        }
    }
}