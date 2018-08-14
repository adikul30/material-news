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
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;

public class Pinned extends Fragment {

//    NewsSQLite newsSQLite;
    NewsAdapter unreadNewsAdapter;
    RecyclerView recyclerView;
//    SwipeRefreshLayout swipeRefreshLayout;
    TextView emptyState;
    NewsViewModel newsViewModel;

    public Pinned() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pinned, container, false);

//        newsSQLite = new NewsSQLite(getActivity());
        recyclerView = rootView.findViewById(R.id.pinned_recycler_view);
//        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        emptyState = rootView.findViewById(R.id.emptyText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        unreadNewsAdapter = new NewsAdapter(getActivity(), 3);
        recyclerView.setAdapter(new ScaleInAnimationAdapter(unreadNewsAdapter));
//        swipeRefreshLayout.setRefreshing(false);

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        newsViewModel.getAllPinned().observe(this, new Observer<List<NewsArticle>>() {
            @Override
            public void onChanged(@Nullable List<NewsArticle> pinnedList) {
                Log.d("Pinned", String.valueOf(pinnedList.size()));
                unreadNewsAdapter.setList(pinnedList);
            }
        });

        return rootView;
    }

/*    public class getNewsFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            pinnedArrayList = newsSQLite.getPinnedRows();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("MessageFragment", "onPostExecute messagesArrayList size=" + pinnedArrayList.size());
            if (pinnedArrayList.size() == 0) {

            } else {
                unreadNewsAdapter = new NewsAdapter(getActivity(), 3, pinnedArrayList);
                recyclerView.setAdapter(new ScaleInAnimationAdapter(unreadNewsAdapter));
                unreadNewsAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }*/
}
