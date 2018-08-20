package kulkarni.aditya.materialnews.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "pinned")
public class Pinned {

    @PrimaryKey
    @NonNull
    private String title;

    private String description;

    private String author;

    private String url;

    private String urlToImage;

    private String publishedAt;

    private long pinnedAt;
    @SerializedName("source")
    private SourceInfo sourceInfo;

    @Ignore
    public Pinned() {

    }

    public Pinned(@NonNull String title, String description, String author, String url, String urlToImage, String publishedAt, long pinnedAt, SourceInfo sourceInfo) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.pinnedAt = pinnedAt;
        this.sourceInfo = sourceInfo;
    }

/*    public Pinned(String author, @NonNull String title, String description, String url, String urlToImage, String publishedAt, SourceInfo sourceInfo) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.sourceInfo = sourceInfo;
    }*/

    public long getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(long pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

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
}
