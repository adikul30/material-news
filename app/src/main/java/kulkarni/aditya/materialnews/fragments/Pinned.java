package kulkarni.aditya.materialnews.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.NewsAdapter;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.util.Constants;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;

public class Pinned extends Fragment {

    NewsAdapter pinnedAdapter;
    RecyclerView recyclerView;
    TextView emptyState;
    NewsViewModel newsViewModel;
    View rootView;
    private final String TAG = this.getClass().getSimpleName();

    public Pinned() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pinned, container, false);

            recyclerView = rootView.findViewById(R.id.pinned_recycler_view);
            emptyState = rootView.findViewById(R.id.emptyText);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            pinnedAdapter = new NewsAdapter(getActivity(), Constants.PINNED);
            recyclerView.setAdapter(new ScaleInAnimationAdapter(pinnedAdapter));

            newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
            newsViewModel.getAllPinned().observe(this, new Observer<List<NewsArticle>>() {
                @Override
                public void onChanged(@Nullable List<NewsArticle> pinnedList) {
                    Log.d(TAG, String.valueOf(pinnedList.size()));
                    for (NewsArticle item : pinnedList) {
                        Log.d(TAG, item.getTitle());
                        Log.d(TAG, String.valueOf(item.getPinned()));
                    }
                    pinnedAdapter.setList(pinnedList);
                }
            });
        }

        return rootView;
    }
}
