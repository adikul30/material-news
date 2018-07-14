package kulkarni.aditya.materialnews.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.adapters.CustomPagerAdapter;
import kulkarni.aditya.materialnews.network.ScheduleServiceHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private GoogleApiClient mGoogleApiClient;
    LottieAnimationView animationView;
    NavigationView navigationView;
    FloatingActionButton fab;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        //Adding Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Articles"));
        tabLayout.addTab(tabLayout.newTab().setText("Blogs"));
        tabLayout.addTab(tabLayout.newTab().setText("Pinned"));

        //Setting ViewPager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerActivity);
        final CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(0, true);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        mUsername = ANONYMOUS;
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API)
//                .addApi(AppInvite.API)
//                .build();
//
//        // Initialize Firebase Auth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(new Intent(this, SignIn.class));
//            finish();
//            return;
//        } else {
//            mUsername = mFirebaseUser.getDisplayName();
//        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, FilterSources.class));
            }
        });

        if (getIntent().getExtras() != null) {
            if (getIntent().getBooleanExtra("isFirstTime", false)) {
                Toasty.info(this, "Click the floating button to select your own News Sources", Toast.LENGTH_LONG).show();
            }
        }

        ScheduleServiceHelper.scheduleBackgroundSync(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //  super.onBackPressed();
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
