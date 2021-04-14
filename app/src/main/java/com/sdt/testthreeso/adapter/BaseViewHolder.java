package com.sdt.testthreeso.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ClassName BaseViewHolder
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 15:02
 * @Version 1.0
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

    private OnItemClickListener onItemClickListener;
    private OnItemFocusChangeListener onItemFocusChangeListener;

    public BaseViewHolder(@NonNull View itemView, OnItemClickListener itemClickListener,
                          OnItemFocusChangeListener onItemFocusChangeListener) {
        super(itemView);
        this.onItemClickListener = itemClickListener;
        this.onItemFocusChangeListener = onItemFocusChangeListener;
        itemView.setOnClickListener(this);
        itemView.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View itemview) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(itemview, getLayoutPosition());
        }
    }

    @Override
    public void onFocusChange(View itemview, boolean hasFocus) {
        if (onItemFocusChangeListener != null) {
            onItemFocusChangeListener.onItemFocusChange(itemview, hasFocus, getLayoutPosition());
        }
    }
}
