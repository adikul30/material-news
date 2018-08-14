package kulkarni.aditya.materialnews.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;

public class NewsViewModel extends AndroidViewModel {

    private LiveData<List<NewsArticle>> newsList, pinnedList;
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

    public LiveData<List<NewsArticle>> getAllPinned() {
        return mDb.newsDao().getPinned();
    }
}
