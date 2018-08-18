package kulkarni.aditya.materialnews.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.model.SourceInfo;
import kulkarni.aditya.materialnews.model.Sources;
import kulkarni.aditya.materialnews.util.MyTypeConverter;


@Database(entities = {NewsArticle.class, Sources.class, Pinned.class}, version = 1, exportSchema = false)
@TypeConverters({MyTypeConverter.class})
public abstract class DatabaseRoom extends RoomDatabase {

    private static final String TAG = DatabaseRoom.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "news.db";
    private static DatabaseRoom sInstance;


    public static DatabaseRoom getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext()
                        , DatabaseRoom.class
                        , DatabaseRoom.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract NewsDao newsDao();

    public abstract SourcesDao sourcesDao();

    public abstract PinnedDao pinnedDao();

}
