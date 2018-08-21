package kulkarni.aditya.materialnews.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import kulkarni.aditya.materialnews.model.Pinned;

@Dao
public interface PinnedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPinned(Pinned pinned);

    @Delete
    void deletePinned(Pinned pinned);

    @Query("SELECT * FROM pinned  ORDER BY pinnedAt DESC")
    LiveData<List<Pinned>> getPinned();
}
