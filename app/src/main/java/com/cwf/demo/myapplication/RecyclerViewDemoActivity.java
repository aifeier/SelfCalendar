package com.cwf.demo.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    private RecyclerView recyclerView;
    private List<Item> stringList;
    private HomeAdapter homeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        stringList = new ArrayList<Item>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(homeAdapter = new HomeAdapter());
        homeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                OkHttpClientManager.getInstance().downloadFile(stringList.get(position).getUrl()
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
        });
        initData();

    }

    protected void initData() {
        OkHttpClientManager.getInstance().get("http://gank.io/api/data/福利/100/2", new ResultCallBack() {
            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Gson gson = new Gson();
                    stringList = gson.fromJson(jsonObject.getString("results"), new TypeToken<List<Item>>() {
                    }.getType());
                    homeAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    RecyclerViewDemoActivity.this).inflate(R.layout.layout_recycler_view_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Glide.with(RecyclerViewDemoActivity.this)
                    .load(stringList.get(position).getUrl())
                    .animate(R.anim.alpha_enter)
                    .fitCenter()
                    .into(holder.tv);
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView tv;

            public MyViewHolder(View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("onclick", "position: " + getLayoutPosition());
                        if (onItemClickListener != null)
                            onItemClickListener.onClick(getLayoutPosition());
                    }
                });
                tv = (ImageView) view.findViewById(R.id.img);
            }
        }
    }

    interface OnItemClickListener {
        void onClick(int position);
    }
}
