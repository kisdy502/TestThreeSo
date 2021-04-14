package com.sdt.testthreeso.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.sdt.testthreeso.R;
import com.sdt.testthreeso.bean.Category;

import java.util.List;

/**
 * @ClassName CategoryAdapter
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 11:22
 * @Version 1.0
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    private List<Category> categoryList;
    private Context context;
    private int playingPos = -1;

    private OnItemFocusChangeListener itemFocusChangeListener;
    private OnItemClickListener itemClickListener;

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener itemFocusChangeListener) {
        this.itemFocusChangeListener = itemFocusChangeListener;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        playingPos = -1;
    }

    public void setPlayingPos(int playingPos) {
        this.playingPos = playingPos;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new VH(view, itemClickListener, itemFocusChangeListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category category = categoryList.get(position);
        holder.tvName.setText(category.getName());
        if (playingPos == position) {
            holder.imgPlayTag.setVisibility(View.VISIBLE);
            holder.tvName.setTextColor(Color.parseColor("#CF3333"));
        } else {
            holder.imgPlayTag.setVisibility(View.GONE);
            holder.tvName.setTextColor(Color.parseColor("#ACACAF"));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        private OnItemClickListener onItemClickListener;
        private OnItemFocusChangeListener onItemFocusChangeListener;

        private AppCompatImageView imgPlayTag;
        private TextView tvName;

        public VH(@NonNull View itemView, OnItemClickListener itemClickListener,
                  OnItemFocusChangeListener onItemFocusChangeListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgPlayTag = itemView.findViewById(R.id.img_playing_tag);
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
            if (hasFocus) {
                tvName.setTextColor(Color.parseColor("#ACACAF"));
                if (getLayoutPosition() == playingPos) {
                    imgPlayTag.setImageResource(R.drawable.icon_boot_focused);
                }
            } else {
                if (getLayoutPosition() == playingPos) {
                    imgPlayTag.setImageResource(R.drawable.icon_boot_default);
                    tvName.setTextColor(Color.parseColor("#CF3333"));
                }
            }
            if (onItemFocusChangeListener != null) {
                onItemFocusChangeListener.onItemFocusChange(itemview, hasFocus, getLayoutPosition());
            }
        }
    }


}
