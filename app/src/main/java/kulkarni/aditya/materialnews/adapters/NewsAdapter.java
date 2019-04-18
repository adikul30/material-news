package kulkarni.aditya.materialnews.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.data.AppExecutor;
import kulkarni.aditya.materialnews.data.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.util.Constants;

/**
 * Created by adicool on 21/5/17.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private List<NewsArticle> newsList;
    private Context mContext;
    private String mType;
    private DatabaseRoom mDb;
    private LottieAnimationView animationView;
    private Bundle bundle;

    public NewsAdapter(Context activityContext, String type) {
        mContext = activityContext;
        mDb = DatabaseRoom.getsInstance(mContext);
        mType = type;
        this.newsList = new ArrayList<>();
    }

    public NewsAdapter(Context activityContext, String type, LottieAnimationView animationView) {
        mContext = activityContext;
        mDb = DatabaseRoom.getsInstance(mContext);
        mType = type;
        this.newsList = new ArrayList<>();
        this.animationView = animationView;
    }

    public void setList(List<NewsArticle> newsArticles) {
        Log.d(TAG, String.valueOf(newsArticles.size()));
        newsList = new ArrayList<>();
        this.newsList = newsArticles;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.title.setText(newsList.get(position).getTitle());
        holder.source.setText(newsList.get(position).getSourceInfo().getName());
        holder.description.setText(newsList.get(position).getDescription());

        Glide.with(mContext.getApplicationContext())
                .load(newsList.get(position).getUrlToImage())
                .into(holder.imageView);

//        holder.publishedAt.setText(UtilityMethods.getTimeAgo(newsList.get(position).getPublishedAt()));

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
        return newsList == null ? 0 : newsList.size();
    }

    public void launchCustomTab(String url) {
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
                    String url = newsList.get(getAdapterPosition()).getUrl();
                    launchCustomTab(url);
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mType.equals(Constants.UNREAD)) {
                        AppExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                NewsArticle model = newsList.get(getAdapterPosition());
                                mDb.pinnedDao().addPinned(
                                        new Pinned(model.getTitle(),
                                                model.getDescription(),
                                                model.getAuthor(),
                                                model.getUrl(),
                                                model.getUrlToImage(),
                                                model.getPublishedAt(),
                                                System.currentTimeMillis(),
                                                model.getSourceInfo()));
                            }
                        });
                        animationView.setVisibility(View.VISIBLE);
                        animationView.setAnimation("TwitterHeart.json");
                        animationView.loop(false);
                        animationView.playAnimation();
                        animationView.addAnimatorListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animationView.cancelAnimation();
                                animationView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                }
            });
        }
    }
}