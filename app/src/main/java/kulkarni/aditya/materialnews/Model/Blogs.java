package kulkarni.aditya.materialnews.Model;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by adicool on 09-11-2017.
 */

public class Blogs {

    private String blogName,blogUrl;

    public Blogs(String blogName, String blogUrl) {
        this.blogName = blogName;
        this.blogUrl = blogUrl;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public static Comparator<Blogs> comparator = new Comparator<Blogs>() {
        @Override
        public int compare(Blogs b1, Blogs b2) {

            String firstBlog = b1.getBlogName().toLowerCase();
            String secondBlog = b2.getBlogName().toLowerCase();

            return firstBlog.compareTo(secondBlog);
        }
    };
}
