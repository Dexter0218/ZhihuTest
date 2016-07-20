package com.dexter0218.zhihu.observable;

import com.dexter0218.zhihu.bean.DailyNews;

import java.util.List;
import rx.Observable;

/**
 * Created by Dexter0218 on 2016/7/20.
 */
public class NewsListFromDatabaseObservable {
    public static Observable<List<DailyNews>> ofDate(String date){
        return Observable.create(subscriber -> {
            // TODO: 2016/7/20  
//           List<DailyNews> newsList =
        });
    }


}
