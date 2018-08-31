package kulkarni.aditya.materialnews.ui;

import android.animation.Animator;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import kulkarni.aditya.materialnews.R;
import kulkarni.aditya.materialnews.db.DatabaseRoom;
import kulkarni.aditya.materialnews.model.NewsArticle;
import kulkarni.aditya.materialnews.model.Pinned;
import kulkarni.aditya.materialnews.util.AppExecutor;
import kulkarni.aditya.materialnews.util.Constants;

/**
 * Created by adicool on 21/5/17.
 */

public class NewsAdapter extends PagedListAdapter<NewsArticle,NewsAdapter.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();
    private List<NewsArticle> newsList;
    private Context mContext;
    private String mType;
    private DatabaseRoom mDb;
    private LottieAnimationView animationView;


    public static final DiffUtil.ItemCallback<NewsArticle> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<NewsArticle>() {
                @Override
                public boolean areItemsTheSame(@NonNull NewsArticle oldUser, @NonNull NewsArticle newUser) {
                    return oldUser.getTitle().equals(newUser.getTitle());
                }

                @Override
                public boolean areContentsTheSame(@NonNull NewsArticle oldUser, @NonNull NewsArticle newUser) {
                    return oldUser == newUser;
                }
            };

    public NewsAdapter(Context context, String type) {
        super(DIFF_CALLBACK);
        this.mContext = context;
        mDb = DatabaseRoom.getsInstance(mContext);
        mType = type;
    }

    public NewsAdapter(Context activityContext, String type, LottieAnimationView animationView) {
        super(DIFF_CALLBACK);
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
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_constraint, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.title.setText(getItem(position).getTitle());
        holder.source.setText(getItem(position).getSourceInfo().getName());
        holder.description.setText(getItem(position).getDescription());

        Glide.with(mContext.getApplicationContext())
                .load(getItem(position).getUrlToImage())
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

/*    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }*/

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, source, publishedAt;
        ConstraintLayout rootLayout;
        ImageView imageView;
        ImageButton imageButton, shareButton;
        RelativeLayout favLayout;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.article_image);
            rootLayout = itemView.findViewById(R.id.root_layout);
            title = itemView.findViewById(R.id.article_title);
            source = itemView.findViewById(R.id.article_source);
//            publishedAt = itemView.findViewById(R.id.article_date);
            imageButton = itemView.findViewById(R.id.favorite_button);
            shareButton = itemView.findViewById(R.id.share_button);
//            favLayout = itemView.findViewById(R.id.fav_button_layout);
            description = itemView.findViewById(R.id.article_description);
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = getItem(getAdapterPosition()).getUrl();
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
                    if (mType.equals(Constants.UNREAD)) {
                        AppExecutor.getInstance().diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                NewsArticle model = getItem(getAdapterPosition());
                                mDb.pinnedDao().addPinned(
                                        new Pinned(model.getAuthor(),
                                                model.getTitle(),
                                                model.getDescription(),
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

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"hahaha",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}