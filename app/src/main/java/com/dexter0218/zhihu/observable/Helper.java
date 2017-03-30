package com.dexter0218.zhihu.observable;

import com.annimon.stream.Optional;
import com.dexter0218.zhihu.support.lib.Http;

import java.io.IOException;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Dexter0218 on 2016/7/21.
 */
public class Helper {
    private Helper() {

    }

    @DebugLog
    static Observable<String> getHtml(String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(url));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String url, String suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String url, int suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String baseUrl, String key, String value) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(baseUrl, key, value));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static <T> Observable<T> toNonempty(Observable<Optional<T>> optionalObservable) {
        return optionalObservable.filter(Optional::isPresent).map(Optional::get);
    }

}
