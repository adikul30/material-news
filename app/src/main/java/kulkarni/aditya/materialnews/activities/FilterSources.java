package kulkarni.aditya.materialnews.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.SourcesAdapter;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;
import kulkarni.aditya.materialnews.viewmodels.SourcesViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FilterSources extends AppCompatActivity {

    RecyclerView recyclerView;
    SourcesAdapter sourcesAdapter;
    List<Sources> sourcesArrayList;
    NewsSQLite newsSQLite;
    DatabaseRoom mDb;
    SourcesViewModel sourcesViewModel;
    NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_sources);
        setTitle("Select Sources");
        newsSQLite = new NewsSQLite(this);
        mDb = DatabaseRoom.getsInstance(this);

        sourcesViewModel = ViewModelProviders.of(this).get(SourcesViewModel.class);
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView = findViewById(R.id.sources_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> baseSource = new ArrayList<>(Arrays.asList("abc-news", "abc-news-au", "aftenposten", "al-jazeera-english", "ansa", "argaam", "ars-technica", "ary-news", "associated-press", "australian-financial-review", "axios", "bbc-news", "bbc-sport", "bild", "blasting-news-br", "bleacher-report", "bloomberg", "breitbart-news", "business-insider", "business-insider-uk", "buzzfeed", "cbc-news", "cbs-news", "cnbc", "cnn", "cnn-es", "crypto-coins-news", "daily-mail", "der-tagesspiegel", "die-zeit", "el-mundo", "engadget", "entertainment-weekly", "espn", "espn-cric-info", "financial-post", "financial-times", "focus", "football-italia", "fortune", "four-four-two", "fox-news", "fox-sports", "globo", "google-news", "google-news-ar", "google-news-au", "google-news-br", "google-news-ca", "google-news-fr", "google-news-in", "google-news-is", "google-news-it", "google-news-ru", "google-news-sa", "google-news-uk", "goteborgs-posten", "gruenderszene", "hacker-news", "handelsblatt", "ign", "il-sole-24-ore", "independent", "infobae", "info-money", "la-gaceta", "la-nacion", "la-repubblica", "le-monde", "lenta", "lequipe", "les-echos", "liberation", "marca", "mashable", "medical-news-today", "metro", "mirror", "msnbc", "mtv-news", "mtv-news-uk", "national-geographic", "nbc-news", "news24", "new-scientist", "news-com-au", "newsweek", "new-york-magazine", "next-big-future", "nfl-news", "nhl-news", "nrk", "politico", "polygon", "rbc", "recode", "reddit-r-all", "reuters", "rt", "rte", "rtl-nieuws", "sabq", "spiegel-online", "svenska-dagbladet", "t3n", "talksport", "techcrunch", "techcrunch-cn", "techradar", "the-economist", "the-globe-and-mail", "the-guardian-au", "the-guardian-uk", "the-hill", "the-hindu", "the-huffington-post", "the-irish-times", "the-lad-bible", "the-new-york-times", "the-next-web", "the-sport-bible", "the-telegraph", "the-times-of-india", "the-verge", "the-wall-street-journal", "the-washington-post", "time", "usa-today", "vice-news", "wired", "wired-de", "wirtschafts-woche", "xinhua-net", "ynet"));
        sourcesArrayList = new ArrayList<>();

        for (int i = 0; i < baseSource.size(); i++) {
            sourcesArrayList.add(new Sources(baseSource.get(i), false));
        }

        sourcesAdapter = new SourcesAdapter(sourcesArrayList, this);
        recyclerView.setAdapter(sourcesAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_action_bar, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final List<Sources> sources = sourcesAdapter.getSelectedList();
        final int count = sourcesAdapter.getSelectedSize();

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (count != 0) {
                    sourcesViewModel.truncateSources();
                    newsViewModel.truncateNews();
                }
                for (int i = 0; i < sources.size(); i++) {
                    Log.v("selectedlist", sources.get(i).getSource());
                    sourcesViewModel.insertSource(new Sources(sources.get(i).getSource(),true));
                }
                finishActivity();
            }
        });

    }

    private void finishActivity(){
        startActivity(new Intent(this, Home.class));
        finish();
    }
}
