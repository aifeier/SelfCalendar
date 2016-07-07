package com.cwf.demo.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created at 陈 on 2016/7/7.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public abstract class AutoLoadAdapter<T> extends RecyclerView.Adapter<AutoLoadViewHolder> {

    private Context mContext;
    private int mItemLayoutId;
    private AutoLoadViewHolder mAutoLoadViewHolder;
    private List<T> mListData;

    private AutoLoadViewHolder.OnItemClickListener mOnItemClickListener;

    public AutoLoadAdapter(Context context, int itemLayoutId) {
        this.mContext = context;
        this.mItemLayoutId = itemLayoutId;
    }


    @Override
    public AutoLoadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAutoLoadViewHolder = new AutoLoadViewHolder(LayoutInflater.from(
                mContext).inflate(mItemLayoutId, parent, false));
        return mAutoLoadViewHolder;
    }

    @Override
    public int getItemCount() {
        if (mListData == null)
            return 0;
        return mListData.size();
    }

    public void setmListData(List<T> mListData) {
        this.mListData = mListData;
    }

    public AutoLoadViewHolder.OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(AutoLoadViewHolder.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
        if (mAutoLoadViewHolder != null)
            mAutoLoadViewHolder.setmOnItemClickListener(mOnItemClickListener);
    }

    public abstract List<T> getNextPage(int page);

}
