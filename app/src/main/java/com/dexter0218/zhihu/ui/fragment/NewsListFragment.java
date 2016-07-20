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

import com.dexter0218.zhihu.support.Constants;
import com.dexter0218.zhihu.ui.activity.R;
import com.dexter0218.zhihu.bean.DailyNews;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;

/**
 * Created by Dexter0218 on 2016/7/19.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer {
    private List<DailyNews> newsList = new ArrayList<>();
    private String date;
    private boolean isToday;

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
        LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(llm);



        return view;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Object o) {

    }

    @Override
    public void onRefresh() {

    }
}
