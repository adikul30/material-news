package kulkarni.aditya.materialnews.Util;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
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
        Fabric.with(this, new Crashlytics());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProductSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        mInstance=this;
    }
}
