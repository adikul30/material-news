package kulkarni.aditya.materialnews.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import kulkarni.aditya.materialnews.model.Sources;

@Dao
public interface SourcesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addSource(Sources source);

    @Query("SELECT * FROM sources")
    LiveData<List<Sources>> getSources();

    @Query("SELECT * FROM sources")
    List<Sources> getSourcesList();

    @Query("SELECT COUNT(*) FROM sources")
    int getSourcesCount();

    @Query("DELETE FROM sources")
    void truncateSources();

}
