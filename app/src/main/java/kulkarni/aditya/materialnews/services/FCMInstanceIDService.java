package kulkarni.aditya.materialnews.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ADMIN on 09-11-2017.
 */

public class FCMInstanceIDService extends FirebaseInstanceIdService {

    String firebaseToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCMToken", firebaseToken);
        getSharedPrefs(firebaseToken);

    }

    public void getSharedPrefs(String token) {
//        SharedPreferences sharedPreferences = this.getSharedPreferences("FCMSharedPref", this.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("auth_token",token);
//        editor.apply();
        Log.d("FCMInstanceIDService", "getSharedPrefs=" + token);
    }
}