package com.cwf.demo.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cwf.libs.okhttplibrary.OkHttpClientManager;
import com.cwf.libs.okhttplibrary.callback.ResultCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 陈 on 2016/7/7.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class RecyclerViewDemoActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private AutoLoadRecyclerView recyclerView;
    private List<Item> stringList;
    private AutoLoadAdapter<Item> homeAdapter;

    private AutoLoadViewHolder.OnItemClickListener onItemClickListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        stringList = new ArrayList<Item>();
        recyclerView = (AutoLoadRecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        onItemClickListener = new AutoLoadViewHolder.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                OkHttpClientManager.getInstance().downloadFile(((Item) recyclerView.getmListData().get(position)).getUrl()
                        , getApplicationContext().getExternalCacheDir().getAbsolutePath()
                        , new ResultCallBack() {
                            @Override
                            public void onFailure(Exception e) {

                            }

                            @Override
                            public void onSuccess(String s) {
                                Toast.makeText(getApplicationContext(), "下载完成！" + s.substring(s.lastIndexOf("/") + 1, s.length()), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDownloading(long downSize, long allSize) {
                                Log.i("onDownloading", downSize + "/" + allSize);
                            }
                        });
            }
        };
        homeAdapter = new AutoLoadAdapter<Item>(this, R.layout.layout_recycler_view_item) {
            @Override
            public List<Item> getNextPage(int page) {
                try {
                    String result = OkHttpClientManager.getInstance().get("http://gank.io/api/data/福利/20/" + page);
                    JSONObject jsonObject = new JSONObject(result);
                    Gson gson = new Gson();
                    return gson.fromJson(jsonObject.getString("results"), new TypeToken<List<Item>>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ArrayList<>();
            }

            @Override
            public void onBindViewHolder(AutoLoadViewHolder holder, int position) {
                holder.setmOnItemClickListener(onItemClickListener);
                holder.setToTextView(R.id.name,
                        ((Item) recyclerView.getmListData().get(position)).getWho());
                holder.setToImageView(R.id.img,
                        ((Item) recyclerView.getmListData().get(position)).getUrl());
            }

        };

        recyclerView.setmAutoLoadAdapter(homeAdapter);
        recyclerView.init(swipeRefreshLayout);

//        initData(page++);

    }

    protected void initData(final int page) {
        OkHttpClientManager.getInstance().get("http://gank.io/api/data/福利/20/" + page, new ResultCallBack() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    List<Item> items = gson.fromJson(jsonObject.getString("results"), new TypeToken<List<Item>>() {
                    }.getType());
                    if (page == 1) {
                        stringList = items;
                    } else {
                        stringList.addAll(items);
                    }
                    homeAdapter.notifyDataSetChanged();
//                    homeAdapter.notifyItemRangeInserted(items.size() - 20, 20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    class MyViewHolder extends AutoLoadViewHolder {

        ImageView tv;
        TextView name;

        public MyViewHolder(View view) {
            super(view);
            tv = (ImageView) view.findViewById(R.id.img);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpClientManager.getInstance().cancel();
    }
}
