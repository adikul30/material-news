package kulkarni.aditya.materialnews.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import kulkarni.aditya.materialnews.Model.NewsArticle;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.Util.NewsSQLite;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash extends AppCompatActivity {

    NewsSQLite newsSQLite;
    ArrayList<NewsArticle> dummyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        newsSQLite = new NewsSQLite(this);
        final SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        final boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

        if (isFirstStart) {
            final Thread thread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        //  Make a new preferences editor
                        SharedPreferences.Editor e = getPrefs.edit();

                        //  Edit preference to make it false because we don't want this to run again
                        e.putBoolean("firstStart", false);

                        //  Apply changes
                        e.apply();

                        newsSQLite.addSource("the-verge");
                        newsSQLite.addSource("techcrunch");
                        newsSQLite.addSource("wired");

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
