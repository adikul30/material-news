package kulkarni.aditya.materialnews.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by maverick on 3/2/18.
 */

public class NewsResponse {
    @SerializedName("status")
    String status;

    @SerializedName("totalResults")
    int totalResults;

    @SerializedName("articles")
    List<NewsArticle> newsArticleList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<NewsArticle> getNewsArticleList() {
        return newsArticleList;
    }

    public void setNewsArticleList(List<NewsArticle> newsArticleList) {
        this.newsArticleList = newsArticleList;
    }
}
