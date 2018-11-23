package kulkarni.aditya.materialnews.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kulkarni.aditya.materialnews.model.Pinned;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PinnedDao {
    @Insert(onConflict = REPLACE)
    void addPinned(Pinned pinned);

    @Delete
    void deletePinned(Pinned pinned);

    @Query("SELECT * FROM pinned  ORDER BY pinnedAt DESC")
    LiveData<List<Pinned>> getPinned();
}
