package com.cwf.demo.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 陈 on 2016/7/7.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class AutoLoadRecyclerView<T> extends RecyclerView {

    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private AutoLoadAdapter mAutoLoadAdapter;
    private int curPage;
    private int lastVisibleItem;
    private Handler mDelivery;
    private List<T> mListData;

    public AutoLoadRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        // 设置ItemAnimator
        setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        setLayoutManager(mLinearLayoutManager);
    }

    public void init(SwipeRefreshLayout refreshLayout) {
        if (refreshLayout == null)
            return;
        mSwipeRefreshLayout = refreshLayout;
        mSwipeRefreshLayout.setColorSchemeColors(
                Color.RED, Color.GREEN, Color.BLUE, Color.BLACK);
        mSwipeRefreshLayout.setRefreshing(true);
        curPage = 1;
        mDelivery = new Handler(Looper.getMainLooper());
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                refresh(curPage++);
            }
        });
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()) {
                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
                    mSwipeRefreshLayout.setRefreshing(true);
                    refresh(curPage++);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        refresh(curPage++);
    }


//    public void init(Context context, View view, int swipeRefreshLayoutid) {
//        init(mContext, (SwipeRefreshLayout) view.findViewById(swipeRefreshLayoutid));
//    }

//    public void init(Context context, SwipeRefreshLayout refreshLayout) {
//        mContext = context;
//        init(mContext, refreshLayout, new LinearLayoutManager(mContext));
//    }

//    public void init(Context context, SwipeRefreshLayout refreshLayout, LinearLayoutManager linearLayoutManager) {
//        mContext = context;
//        mLinearLayoutManager = linearLayoutManager;
//        mSwipeRefreshLayout = refreshLayout;
//        mSwipeRefreshLayout.setColorSchemeColors(
//                Color.RED, Color.GREEN, Color.BLUE, Color.BLACK);
//        mSwipeRefreshLayout.setRefreshing(true);
//        init();
//    }

    private void init() {
        curPage = 1;
        mDelivery = new Handler(Looper.getMainLooper());
        setLayoutManager(mLinearLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                refresh(curPage++);
            }
        });
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()) {
                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
                    mSwipeRefreshLayout.setRefreshing(true);
                    refresh(curPage++);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }


    public void refresh(final int page) {
        if (!executing)
            new LoadNextPage(page).execute();
    }

    public List<T> getmListData() {
        return mListData;
    }

    /*设置适配器*/
    public void setmAutoLoadAdapter(AutoLoadAdapter mAutoLoadAdapter) {
        this.mAutoLoadAdapter = mAutoLoadAdapter;
        setAdapter(mAutoLoadAdapter);
    }

    private Boolean executing = false;

    private class LoadNextPage extends AsyncTask<Void, Integer, List<T>> {
        private int page;

        public LoadNextPage(int page) {
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            executing = true;
        }

        @Override
        protected List<T> doInBackground(Void... params) {
            if (mAutoLoadAdapter == null)
                return new ArrayList<>();
            return mAutoLoadAdapter.getNextPage(page);
        }

        @Override
        protected void onPostExecute(List<T> list) {
            if (mAutoLoadAdapter == null)
                return;
            if (mListData == null)
                mListData = new ArrayList<>();
            if (page == 1)
                mListData.clear();
            mListData.addAll(list);
            mAutoLoadAdapter.setmListData(mListData);
            mAutoLoadAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            executing = false;
        }
    }
}
