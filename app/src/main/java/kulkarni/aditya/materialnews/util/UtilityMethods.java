package kulkarni.aditya.materialnews.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class UtilityMethods {
    public static CharSequence getTimeAgo(String time) {
        // From https://stackoverflow.com/a/29502282
        // Answer suggests to replace 'Z' with a generic character X
        // It'd handle ISO 8601 standard timestamps
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
        //just initializing
        long createdTime = System.currentTimeMillis();
        try {
            createdTime = sdf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DateUtils.getRelativeTimeSpanString(createdTime, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    public static boolean checkNet(Context context) {
        // function to check whether the user is connected to the internet or not
        // requires the ACCESS_NETWORK_STATE permission to be defined in the manifest
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
