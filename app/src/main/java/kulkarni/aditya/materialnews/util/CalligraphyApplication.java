package kulkarni.aditya.materialnews.util;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import kulkarni.aditya.materialnews.BuildConfig;
import kulkarni.aditya.materialnews.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by adicool on 9/5/17.
 */

public class CalligraphyApplication extends Application {

    private static CalligraphyApplication mInstance;
    public static final String TAG=CalligraphyApplication.class.getSimpleName();

    public static synchronized CalligraphyApplication getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProductSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        mInstance=this;
    }
}
