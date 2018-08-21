package kulkarni.aditya.materialnews.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import kulkarni.aditya.materialnews.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity {

    TextView githubLink;
    TextView newsAPiLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("AboutActivity Me");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        githubLink = (TextView)findViewById(R.id.github_link);
        newsAPiLink = (TextView)findViewById(R.id.newsAPILink);
        Typeface font_awesome = Typeface.createFromAsset(getAssets(),getString(R.string.font_awesome));
        githubLink.setTypeface(font_awesome);
        githubLink.setText(R.string.fa_github);
        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent githubRepo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/adikul30"));
                startActivity(githubRepo);
            }
        });
        newsAPiLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newsAPI = new Intent(Intent.ACTION_VIEW, Uri.parse("https://newsapi.org/"));
                startActivity(newsAPI);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
