package kulkarni.aditya.materialnews.model

import java.util.*

/**
 * Created by adicool on 09-11-2017.
 */

class Blogs(var blogName: String?, var blogUrl: String?) {
    companion object {

        var comparator: Comparator<Blogs> = Comparator { b1, b2 ->
            val firstBlog = b1.blogName!!.toLowerCase()
            val secondBlog = b2.blogName!!.toLowerCase()

            firstBlog.compareTo(secondBlog)
        }
    }
}
