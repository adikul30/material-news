package kulkarni.aditya.materialnews.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.model.SourceInfo;

public class NewsViewModel extends AndroidViewModel {

//    private LiveData<List<NewsArticle>> newsList, pinnedList;
    private DatabaseRoom mDb;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        mDb = DatabaseRoom.getsInstance(this.getApplication());
    }

    public LiveData<List<NewsArticle>> getAllNewsArticles() {
        return mDb.newsDao().getNews();
    }

    public void insertNewsArticle(final NewsArticle newsArticle) {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().addNews(newsArticle);
            }
        });
    }

    public void truncateNews() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.newsDao().truncateNews();
            }
        });
    }

    public LiveData<List<Pinned>> getAllPinned() {
        return mDb.pinnedDao().getPinned();
    }

    public void insertBrowserPinned(final String url){
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.pinnedDao().addPinned(new Pinned(url, "", "John Doe", url, "", "", System.currentTimeMillis(), new SourceInfo("browser","chrome")));
            }
        });
    }
}
