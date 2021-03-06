package com.dexter0218.zhihu.observable;

import com.dexter0218.zhihu.ZhihuDailyApplication;
import com.dexter0218.zhihu.bean.DailyNews;

import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;

/**
 * Created by Dexter0218 on 2016/7/20.
 */
public class NewsListFromDatabaseObservable {
    @DebugLog
    public static Observable<List<DailyNews>> ofDate(String date) {
        return Observable.create(subscriber -> {
            // TODO: 2016/7/20  
            List<DailyNews> newsList = ZhihuDailyApplication.getDataSource().newsOfTheDay(date);
            if (newsList != null) {
                subscriber.onNext(newsList);
            }
            subscriber.onCompleted();
        });
    }


}
