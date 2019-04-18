package kulkarni.aditya.materialnews.activities;

import android.app.SearchManager;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.CustomPagerAdapter;
import kulkarni.aditya.materialnews.fragments.BlogFragment;
import kulkarni.aditya.materialnews.fragments.PinnedFragment;
import kulkarni.aditya.materialnews.fragments.UnreadFragment;
import kulkarni.aditya.materialnews.network.ScheduleServiceHelper;
import kulkarni.aditya.materialnews.util.Constants;
import kulkarni.aditya.materialnews.viewmodels.NewsViewModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LottieAnimationView animationView;
    NavigationView navigationView;
    FloatingActionButton fab;
    NewsViewModel newsViewModel;
    ViewPager viewPager;
    CoordinatorLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)) {
                handleFavouriteFromBrowser(intent);
            }
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getBooleanExtra("isFirstTime", false)) {
                Snackbar.make(rootLayout, "Click the floating button to select your own News Sources", Snackbar.LENGTH_LONG).show();
            }
            else if (getIntent().getStringExtra(Constants.URL) != null) {
                launchCustomTab(getIntent().getStringExtra(Constants.URL));
            }
        }

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);


//        animationView = findViewById(R.id.animation_view);
        navigationView = findViewById(R.id.nav_view);
        rootLayout = findViewById(R.id.root_layout);
        fab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }

        // App Theme Customization
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewPagerActivity);
        final CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new UnreadFragment(),"Articles");
        pagerAdapter.addFragment(new BlogFragment(),"Blogs");
        pagerAdapter.addFragment(new PinnedFragment(),"Pinned");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0, true);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.this.startActivity(new Intent(Home.this, FilterSources.class));
            }
        });

        ScheduleServiceHelper.scheduleBackgroundSync(this);

    }

    private void handleFavouriteFromBrowser(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Log.d("HomeActivity", sharedText);
            newsViewModel.insertBrowserPinned(sharedText);
            viewPager.setCurrentItem(2, true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.option_search).getActionView();

        ComponentName componentName = new ComponentName(this, SearchResults.class);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_sources) {
            startActivity(new Intent(Home.this, FilterSources.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(Home.this, About.class));
        } else if (id == R.id.nav_share) {
            String shareUrl = getString(R.string.invitation_message);
//            Intent sendIntent = new Intent(Intent.ACTION_SEND);
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
//            sendIntent.setType("text/plain");
//            startActivity(sendIntent);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share this app to"));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //  super.onBackPressed();
        }
        return true;
    }

    public void launchCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_arrow_back_white_24dp));
        builder.setToolbarColor(this.getResources().getColor(R.color.colorPrimary));
        builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
