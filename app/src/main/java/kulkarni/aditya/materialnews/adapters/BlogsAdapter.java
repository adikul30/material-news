package kulkarni.aditya.materialnews.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import kulkarni.aditya.materialnews.R
import kulkarni.aditya.materialnews.model.Blogs

/**
 * Created by adicool on 09-11-2017.
 */

class BlogsAdapter(private val blogsArrayList: ArrayList<Blogs>?, private val mContext: Context) : RecyclerView.Adapter<BlogsAdapter.ViewHolder>() {
    private var elementPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blogName.text = blogsArrayList!![position].blogName
    }

    override fun getItemCount(): Int {
        return blogsArrayList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var blogName: TextView

        init {
            blogName = itemView.findViewById<View>(R.id.blog_source) as TextView
            blogName.setOnClickListener {
                elementPosition = adapterPosition
                val model = blogsArrayList!![elementPosition]
                val builder = CustomTabsIntent.Builder()
                builder.addDefaultShareMenuItem()
                builder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.resources,
                        R.mipmap.ic_arrow_back_white_24dp))
                builder.setToolbarColor(mContext.resources.getColor(R.color.colorPrimary))
                builder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left)
                builder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(mContext, Uri.parse(model.blogUrl))
            }
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
}
