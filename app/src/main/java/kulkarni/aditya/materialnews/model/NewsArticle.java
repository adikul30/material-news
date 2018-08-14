package kulkarni.aditya.materialnews.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by adicool on 29/5/17.
 */

@Entity(tableName = "newsarticles")
public class NewsArticle {

/*    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;*/

    private String author;

    @PrimaryKey
    @NonNull
    private String title;

    private String description;

    private String url;

    private String urlToImage;

    private String publishedAt;

    private Boolean isPinned = false;

    @SerializedName("source")
//    @Expose
    private SourceInfo sourceInfo;

    @Ignore
    public NewsArticle() {
        this.isPinned = false;
    }

    public NewsArticle(String author, @NonNull String title, String description, String url, String urlToImage, String publishedAt, SourceInfo sourceInfo) {
//        this.id = id;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.isPinned = false;
        this.sourceInfo = sourceInfo;
    }

//    @NonNull
/*    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }*/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }
}