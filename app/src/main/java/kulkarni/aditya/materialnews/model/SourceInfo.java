package kulkarni.aditya.materialnews.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by maverick on 3/2/18.
 */
@Entity(tableName = "sourceinfo")
public class SourceInfo {

    @PrimaryKey
    @NonNull
    private String id;

    private String name;

    @Ignore
    public SourceInfo() {
    }

    public SourceInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
