package kulkarni.aditya.materialnews.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.PinnedAdapter;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.util.Constants;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;

public class PinnedFragment extends Fragment {

    PinnedAdapter pinnedAdapter;
    RecyclerView recyclerView;
    TextView emptyState;
    NewsViewModel newsViewModel;
    View rootView;
    private final String TAG = this.getClass().getSimpleName();

    public PinnedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pinned, container, false);

            recyclerView = rootView.findViewById(R.id.pinned_recycler_view);
            emptyState = rootView.findViewById(R.id.empty_text);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            pinnedAdapter = new PinnedAdapter(getActivity(), Constants.PINNED);
            recyclerView.setAdapter(new ScaleInAnimationAdapter(pinnedAdapter));

            newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
            newsViewModel.getAllPinned().observe(this, new Observer<List<Pinned>>() {
                @Override
                public void onChanged(@Nullable List<Pinned> pinnedList) {
                    Log.d(TAG, String.valueOf(pinnedList.size()));
                    if(pinnedList.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        pinnedAdapter.setList(pinnedList);
                    }
                }
            });
        }

        return rootView;
    }
}
