package com.dexter0218.zhihu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexter0218.zhihu.ZhihuDailyApplication;
import com.dexter0218.zhihu.adapter.NewsAdapter;
import com.dexter0218.zhihu.observable.NewsListFromDatabaseObservable;
import com.dexter0218.zhihu.observable.NewsListFromZhihuObservable;
import com.dexter0218.zhihu.support.Constants;
import com.dexter0218.zhihu.task.SaveNewsListTask;
import com.dexter0218.zhihu.ui.activity.BaseActivity;
import com.dexter0218.zhihu.ui.activity.R;
import com.dexter0218.zhihu.bean.DailyNews;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dexter0218 on 2016/7/19.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer<List<DailyNews>> {
    private List<DailyNews> newsList = new ArrayList<>();
    private String date;
    private NewsAdapter mAdapter;

    private boolean isToday;
    private boolean isRefreshed = false;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            date = bundle.getString(Constants.BundleKeys.DATE);
            isToday = bundle.getBoolean(Constants.BundleKeys.IS_FIRST_PAGE);
            setRetainInstance(true);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        assert view != null;
        RecyclerView mRecycleView = (RecyclerView) view.findViewById(R.id.news_list);
        mRecycleView.setHasFixedSize(!isToday);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(llm);

        mAdapter = new NewsAdapter(newsList);
        mRecycleView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swip_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        NewsListFromDatabaseObservable.ofDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        refreshIf(shouldRefreshOnVisibilityChange(isVisibleToUser));
    }

    private void refreshIf(boolean prerequisite) {
        if (prerequisite) {
            doRefresh();
        }
    }

    private void doRefresh() {
        getNewsListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private Observable<List<DailyNews>> getNewsListObservable() {
        if (shouldSubScribeToZhihu()) {
            return NewsListFromZhihuObservable.ofDate(date);
        } else {
            //           return NewsListFromAccelerateServerObservable.ofDate(date);
        }
        return null;
    }

    @DebugLog
    private boolean shouldSubScribeToZhihu() {
        return isToday || !shouldUseAccerlerateServer();
    }

    private boolean shouldUseAccerlerateServer() {

        return ZhihuDailyApplication.getSharedPreferences().getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_USE_ACCELERATE_SERVER, false);
    }

    private boolean shouldAutoRefresh() {
        return ZhihuDailyApplication.getSharedPreferences()
                .getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_AUTO_REFRESH, true);
    }

    private boolean shouldRefreshOnVisibilityChange(boolean isVisibleToUser) {
        return isVisibleToUser && shouldAutoRefresh() && !isRefreshed;
    }

    @Override
    public void onCompleted() {
        isRefreshed = true;

        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.updateNewsList(newsList);

        new SaveNewsListTask(newsList).execute();
    }

    @Override
    public void onError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (isAdded()) {
            ((BaseActivity) getActivity()).showSnackbar(R.string.network_error);
        }
    }

    @Override
    public void onNext(List<DailyNews> newsList) {
        this.newsList = newsList;
    }

    @Override
    public void onRefresh() {
            doRefresh();
    }
}
