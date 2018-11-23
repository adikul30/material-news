package kulkarni.aditya.materialnews.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.util.Constants;

public class PinnedAdapter extends RecyclerView.Adapter<PinnedAdapter.ViewHolder> {

    private List<Pinned> pinnedList;
    private Context mContext;
    private String mType;
    private DatabaseRoom mDb;
    private final String TAG = this.getClass().getSimpleName();

    public PinnedAdapter(Context activityContext, String type) {
        mContext = activityContext;
        mDb = DatabaseRoom.getsInstance(mContext);
        mType = type;
        this.pinnedList = new ArrayList<>();
    }

    public void setList(List<Pinned> Pinneds) {
        Log.d(TAG, String.valueOf(Pinneds.size()));
        pinnedList = new ArrayList<>();
        this.pinnedList = Pinneds;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(!pinnedList.get(position).getDescription().equals("")) {

            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(pinnedList.get(position).getTitle());
            holder.description.setText(pinnedList.get(position).getDescription());
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(mContext.getApplicationContext())
                    .load(pinnedList.get(position).getUrlToImage())
                    .into(holder.imageView);

//        holder.publishedAt.setText(UtilityMethods.getTimeAgo(pinnedList.get(position).getPublishedAt()));

        }
        else {
            holder.title.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.description.setText(pinnedList.get(position).getUrl());
        }

        holder.source.setText(pinnedList.get(position).getSourceInfo().getName());

        switch (mType) {
            case Constants.PINNED:
                holder.imageButton.setImageResource(R.drawable.ic_favorite_black_18px);
                break;
            case Constants.UNREAD:
                holder.imageButton.setImageResource(R.drawable.ic_favorite_border_black_18px);
                break;
            case Constants.SEARCH:
                holder.imageButton.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return pinnedList == null ? 0 : pinnedList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, source, publishedAt;
        CardView cardView;
        ImageView imageView;
        ImageButton imageButton;
        RelativeLayout favLayout;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.article_image);
            cardView = itemView.findViewById(R.id.news_card_view);
            title = itemView.findViewById(R.id.article_title);
            source = itemView.findViewById(R.id.article_source);
//            publishedAt = itemView.findViewById(R.id.article_date);
            imageButton = itemView.findViewById(R.id.favorite_button);
            favLayout = itemView.findViewById(R.id.fav_button_layout);
            description = itemView.findViewById(R.id.article_description);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = pinnedList.get(getAdapterPosition()).getUrl();
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.addDefaultShareMenuItem();
                    builder.setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_arrow_back_white_24dp));
                    builder.setToolbarColor(mContext.getResources().getColor(R.color.colorPrimary));
                    builder.setStartAnimations(mContext, R.anim.slide_in_right, R.anim.slide_out_left);
                    builder.setExitAnimations(mContext, R.anim.slide_in_left, R.anim.slide_out_right);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(mContext, Uri.parse(url));
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppExecutor.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.pinnedDao().deletePinned(pinnedList.get(getAdapterPosition()));
                        }
                    });

                }
            });
        }
    }
}