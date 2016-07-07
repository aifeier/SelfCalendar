package com.cwf.demo.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created at 陈 on 2016/7/7.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class AutoLoadViewHolder extends RecyclerView.ViewHolder {
    private OnItemClickListener mOnItemClickListener;
    private View itemView;
    private SparseArray<View> mViews;//存储item中的子view

    public AutoLoadViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        mViews = new SparseArray<>();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onClick(getLayoutPosition());
            }
        });
    }

    public void setToTextView(int viewId, String msg) {
        ((TextView) findView(viewId)).setText(msg);
    }

    public void setToImageView(int viewId, String imgPath) {
        Glide.with(itemView.getContext())
                .load(imgPath)
                .into((ImageView) findView(viewId));
    }

    private View findView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }


    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }


}
