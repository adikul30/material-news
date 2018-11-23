package kulkarni.aditya.materialnews.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by adicool on 15/7/17.
 */
@Entity(tableName = "sources")
public class Sources {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    private String source;
    private boolean isSelected;

    public Sources(String source, boolean isSelected) {
        this.source = source;
        this.isSelected = isSelected;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @Ignore
    public Sources() {
    }
}
