package kulkarni.aditya.materialnews.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.Sources;

public class SourcesViewModel extends AndroidViewModel {
    private LiveData<List<Sources>> sourceList;
    private DatabaseRoom mDb;

    public SourcesViewModel(@NonNull Application application) {
        super(application);
        mDb = DatabaseRoom.getsInstance(this.getApplication());
        sourceList = mDb.sourcesDao().getSources();
    }

    public LiveData<List<Sources>> getAllSources() {
        return sourceList;
    }

    public void insertSource(final Sources source){
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.sourcesDao().addSource(source);
            }
        });
    }

    public void truncateSources() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.sourcesDao().truncateSources();
            }
        });
    }
}
