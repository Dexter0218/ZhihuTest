package com.dexter0218.zhihu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.dexter0218.zhihu.support.Constants;
import com.dexter0218.zhihu.ui.activity.R;
import com.dexter0218.zhihu.bean.DailyNews;
import com.dexter0218.zhihu.bean.Question;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dexter0218 on 2016/7/20.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.CardViewHolder> {
    private List<DailyNews> newsList;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.noimage)
            .showImageOnFail(R.drawable.noimage)
            .showImageForEmptyUri(R.drawable.lks_for_blank_url)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public NewsAdapter(List<DailyNews> newsList) {
        this.newsList = newsList;

        setHasStableIds(true);
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.news_list_item, parent, false);

        return new CardViewHolder(itemView, new CardViewHolder.ClickResponseListener() {
            @Override
            public void onWholeClick(int position) {
                browse(context, position);
            }

            @Override
            public void onOverflowClick(View v, int position) {
                // TODO: 2016/7/20
            }
        });

    }
    public void setNewsList(List<DailyNews> newsList) {
        this.newsList = newsList;
    }
    public void updateNewsList(List<DailyNews> newsList) {
        setNewsList(newsList);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        DailyNews dailyNews = newsList.get(position);
        imageLoader.displayImage(dailyNews.getThumbnailUrl(), holder.newsImage, options, animateFirstListener);
        if (dailyNews.getQuestions().size() > 1) {
            holder.questionTitle.setText(dailyNews.getDailyTitle());
            holder.dailyTitle.setText(Constants.Strings.MULTIPLE_DISCUSSION);
        } else {
            holder.questionTitle.setText(dailyNews.getQuestions().get(0).getTitle());
            holder.dailyTitle.setText(dailyNews.getDailyTitle());
        }
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public long getItemId(int position) {
        return newsList.get(position).hashCode();
    }

    private void browse(Context context, int position) {
        DailyNews dailyNews = newsList.get(position);

        if (dailyNews.hasMultipleQuestions()) {
            AlertDialog dialog = createDialog(context,
                    dailyNews,
                    makeGoToZhihuDialogClickListener(context, dailyNews));
            dialog.show();
        } else {

            // TODO: 2016/7/20
//            goToZhihu(context, dailyNews.getQuestions().get(0).getUrl());
        }
    }

    private AlertDialog createDialog(Context context, DailyNews dailyNews, DialogInterface.OnClickListener listener) {
        String[] questionTitles = getQuestionTitlesAsStringArray(dailyNews);

        return new AlertDialog.Builder(context)
                .setTitle(dailyNews.getDailyTitle())
                .setItems(questionTitles, listener)
                .create();
    }

    private String[] getQuestionTitlesAsStringArray(DailyNews dailyNews) {
        return Stream.of(dailyNews.getQuestions()).map(Question::getTitle).toArray(String[]::new);
    }

    private DialogInterface.OnClickListener makeGoToZhihuDialogClickListener(Context context, DailyNews dailyNews) {
        return (dialog, which) -> {
            String questionTitle = dailyNews.getQuestions().get(which).getTitle(),
                    questionUrl = dailyNews.getQuestions().get(which).getUrl();
            // TODO: 2016/7/20
//            shareQuestion(context, questionTitle, questionUrl);
        };
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView newsImage;
        public TextView questionTitle;
        public TextView dailyTitle;
        public ImageView overflow;
        public ClickResponseListener mClickResponseListener;

        public CardViewHolder(View itemView, ClickResponseListener clickResponseListener) {
            super(itemView);
            this.mClickResponseListener = clickResponseListener;

            newsImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            questionTitle = (TextView) itemView.findViewById(R.id.question_title);
            dailyTitle = (TextView) itemView.findViewById(R.id.daily_title);
            overflow = (ImageView) itemView.findViewById(R.id.card_share_overflow);
            itemView.setOnClickListener(this);
            overflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == overflow) {
                mClickResponseListener.onOverflowClick(v, getAdapterPosition());
            } else {
                mClickResponseListener.onWholeClick(getAdapterPosition());
            }
        }

        public interface ClickResponseListener {
            void onWholeClick(int position);

            void onOverflowClick(View v, int position);
        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
