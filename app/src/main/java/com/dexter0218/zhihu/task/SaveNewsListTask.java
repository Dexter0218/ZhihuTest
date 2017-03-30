package com.dexter0218.zhihu.task;

import android.os.AsyncTask;

import com.dexter0218.zhihu.ZhihuDailyApplication;
import com.dexter0218.zhihu.bean.DailyNews;
import com.dexter0218.zhihu.db.DailyNewsDataSource;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Wupeng on 2016/7/22.
 */
public class SaveNewsListTask extends AsyncTask<Void, Void, Void> {
    private List<DailyNews> newsList;

    public SaveNewsListTask(List<DailyNews> newsList) {
        this.newsList = newsList;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (newsList != null && newsList.size() > 0) {
            saveNewsList(newsList);
        }

        return null;
    }

    private void saveNewsList(List<DailyNews> newsList) {
        DailyNewsDataSource dataSource = ZhihuDailyApplication.getDataSource();
        String date = newsList.get(0).getDate();

        List<DailyNews> originalData = dataSource.newsOfTheDay(date);

        if (originalData == null || !originalData.equals(newsList)) {
            dataSource.insertOrUpdateNewsList(date, new GsonBuilder().create().toJson(newsList));
        }
    }
}