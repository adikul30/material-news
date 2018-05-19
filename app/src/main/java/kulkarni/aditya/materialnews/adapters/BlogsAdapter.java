package kulkarni.aditya.materialnews.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.model.Blogs;

/**
 * Created by adicool on 09-11-2017.
 */

public class BlogsAdapter extends RecyclerView.Adapter<BlogsAdapter.ViewHolder> {

    private ArrayList<Blogs> blogsArrayList;
    private Context mContext;
    private int elementPosition;

    public BlogsAdapter(ArrayList<Blogs> blogsArrayList, Context mContext) {
        this.blogsArrayList = blogsArrayList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_item,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.blogName.setText(blogsArrayList.get(position).getBlogName());
    }

    @Override
    public int getItemCount() {
        if(blogsArrayList == null){
            return 0;
        } else {
            return blogsArrayList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView blogName;

        public ViewHolder(View itemView) {
            super(itemView);
            blogName = (TextView) itemView.findViewById(R.id.blog_source);
            blogName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    elementPosition = getAdapterPosition();
                    Blogs model = blogsArrayList.get(elementPosition);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.addDefaultShareMenuItem();
                    builder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_arrow_back_white_24dp));
                    builder.setToolbarColor(mContext.getResources().getColor(R.color.colorPrimary));
                    builder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
                    builder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(mContext, Uri.parse(model.getBlogUrl()));
                }
            });
        }

    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
