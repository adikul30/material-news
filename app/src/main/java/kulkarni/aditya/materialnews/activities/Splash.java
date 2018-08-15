package kulkarni.aditya.materialnews.activities;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.data.NewsSQLite;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;
import kulkarni.aditya.materialnews.viewmodels.SourcesViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash extends AppCompatActivity {

    SourcesViewModel sourcesViewModel;
    NewsViewModel newsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sourcesViewModel = ViewModelProviders.of(this).get(SourcesViewModel.class);
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        final SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        if (isFirstStart) {
            final Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        newsViewModel.truncateNews();
                        sourcesViewModel.truncateSources();
                        //  Make a new preferences editor
                        SharedPreferences.Editor e = getPrefs.edit();

                        //  Edit preference to make it false because we don't want this to run again
                        e.putBoolean("firstStart", false);

                        //  Apply changes
                        e.apply();

                        sourcesViewModel.insertSource(new Sources("the-verge",true));
                        sourcesViewModel.insertSource(new Sources("techcrunch",true));
                        sourcesViewModel.insertSource(new Sources("wired",true));

                        final Intent intent = new Intent(Splash.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            thread.start();

        } else {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = new Intent(Splash.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            thread.start();

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
