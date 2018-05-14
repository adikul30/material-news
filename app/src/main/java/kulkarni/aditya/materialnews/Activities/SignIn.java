package kulkarni.aditya.materialnews.Activities;

/**
 * Created by adicool on 25/5/17.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import es.dmoral.toasty.Toasty;
import kulkarni.aditya.materialnews.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton mSignInButton;
    private ProgressBar progressBar;
    ConnectivityManager connectivityManager;
    NetworkInfo activeNetwork;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();

        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        for (int i = 0; i < mSignInButton.getChildCount(); i++) {
            View v = mSignInButton.getChildAt(i);

            if (v instanceof TextView)
            {
                TextView tv = (TextView) v;
                tv.setTextSize(14);
                tv.setTextColor(Color.parseColor("#757575"));
                tv.setSingleLine(true);
                tv.setPadding(15, 15, 15, 15);

                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                if(connectivityManager.getActiveNetworkInfo()!=null)
                    signIn();
                else Toasty.error(this,"You are OFFLINE !",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                progressBar.setVisibility(View.VISIBLE);


                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign-In failed
                Toasty.error(this,"Google Sign-In cancelled",Toast.LENGTH_SHORT).show();
                try {
                    Log.e(TAG, result.getStatus().getStatusMessage());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Intent signInIntent = new Intent(SignIn.this,Home.class);
                            signInIntent.putExtra("isFirstTime",true);
                            startActivity(signInIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}