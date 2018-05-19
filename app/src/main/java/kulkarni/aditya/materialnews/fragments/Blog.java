package kulkarni.aditya.materialnews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.BlogsAdapter;
import kulkarni.aditya.materialnews.model.Blogs;


public class Blog extends Fragment {

    RecyclerView recyclerView;
    BlogsAdapter blogsAdapter;
    ArrayList<Blogs> blogArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blogs, container, false);
        initBlogs();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.blogs_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        blogsAdapter = new BlogsAdapter(blogArrayList,getActivity());
        recyclerView.setAdapter(new ScaleInAnimationAdapter(blogsAdapter));
        blogsAdapter.notifyDataSetChanged();

        return rootView;
    }

    private void initBlogs(){
        blogArrayList = new ArrayList<>();
        blogArrayList.add(new Blogs("Google","https://blog.google"));
        blogArrayList.add(new Blogs("Firebase","https://firebase.googleblog.com"));
        blogArrayList.add(new Blogs("Android Developers","https://android-developers.googleblog.com/?m=1"));
        blogArrayList.add(new Blogs("Twitter Engineering","https://blog.twitter.com/engineering/en_us.html"));
        blogArrayList.add(new Blogs("facebook research","https://research.fb.com/blog/"));
        blogArrayList.add(new Blogs("Airbnb","https://blog.atairbnb.com"));
        blogArrayList.add(new Blogs("GitHub Engineering","https://githubengineering.com"));
        blogArrayList.add(new Blogs("Spotify Labs","https://labs.spotify.com"));
        blogArrayList.add(new Blogs("Mozilla","https://hacks.mozilla.org"));
        blogArrayList.add(new Blogs("Google Developer","https://developers.googleblog.com/?m=1"));
        blogArrayList.add(new Blogs("Netflix","https://medium.com/netflix-techblog"));
        blogArrayList.add(new Blogs("Dropbox","https://blogs.dropbox.com/tech/"));
        blogArrayList.add(new Blogs("Pinterest","https://engineering.pinterest.com"));
        blogArrayList.add(new Blogs("LinkedIn","https://engineering.linkedin.com"));
        blogArrayList.add(new Blogs("Square","https://medium.com/square-corner-blog"));
        blogArrayList.add(new Blogs("Atlassian","https://developer.atlassian.com/blog/"));
        blogArrayList.add(new Blogs("Amazon AWS","https://aws.amazon.com/blogs/aws/"));
        blogArrayList.add(new Blogs("Docker","https://blog.docker.com/category/engineering/"));
        blogArrayList.add(new Blogs("Evernote","https://blog.evernote.com/tech/"));
        blogArrayList.add(new Blogs("Foursquare","https://engineering.foursquare.com/"));
        blogArrayList.add(new Blogs("HackerEarth","http://engineering.hackerearth.com/"));
        blogArrayList.add(new Blogs("Heroku","https://blog.heroku.com/engineering"));
        blogArrayList.add(new Blogs("Instagram","https://engineering.instagram.com/"));
        blogArrayList.add(new Blogs("MongoDB","https://engineering.mongodb.com/"));
        blogArrayList.add(new Blogs("Quora","https://engineering.quora.com/"));
        blogArrayList.add(new Blogs("Red Hat","https://developerblog.redhat.com"));
        blogArrayList.add(new Blogs("Slack","https://slack.engineering/"));
        blogArrayList.add(new Blogs("Stack Overflow"," https://stackoverflow.blog/engineering/"));
        blogArrayList.add(new Blogs("Uber","http://eng.uber.com/"));
        blogArrayList.add(new Blogs("GitHub","https://blog.github.com/"));
        blogArrayList.add(new Blogs("Google Design","https://design.google/"));
        blogArrayList.add(new Blogs("OpenAI Blog","https://blog.openai.com/"));

        Collections.sort(blogArrayList,Blogs.comparator);

    }
}
