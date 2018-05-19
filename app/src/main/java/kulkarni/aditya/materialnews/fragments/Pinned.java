package kulkarni.aditya.materialnews.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
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

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.NewsAdapter;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.NewsArticle;

public class Pinned extends Fragment {

    NewsSQLite newsSQLite;
    static NewsAdapter unreadNewsAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView emptyState;
    private ArrayList<NewsArticle> pinnedArrayList;

    public Pinned() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pinned, container, false);

        newsSQLite = new NewsSQLite(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.pinned_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        emptyState = (TextView) rootView.findViewById(R.id.emptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pinnedArrayList = new ArrayList<>();

        new getNewsFromDb().execute();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new getNewsFromDb().execute();
            }
        });

        return rootView;
    }

    public class getNewsFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            pinnedArrayList = newsSQLite.getPinnedRows();

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
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
