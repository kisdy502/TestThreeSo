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
import com.sdt.testthreeso.bean.Channel;

import java.util.List;

/**
 * @ClassName CategoryAdapter
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 11:22
 * @Version 1.0
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.VH> {

    private List<Channel> channelList;
    private Context context;
    private int playingPos = -1;
    private OnItemFocusChangeListener itemFocusChangeListener;
    private OnItemClickListener itemClickListener;

    public void setPlayingPos(int playingPos) {
        this.playingPos = playingPos;
    }

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener itemFocusChangeListener) {
        this.itemFocusChangeListener = itemFocusChangeListener;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ChannelAdapter(Context context, List<Channel> channelList) {
        this.context = context;
        this.channelList = channelList;
        playingPos = -1;

    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new VH(view, itemClickListener, itemFocusChangeListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Channel channel = channelList.get(position);
        holder.tvName.setText(channel.getName());
        if (playingPos == position) {
            holder.imgPlayTag.setVisibility(View.VISIBLE);
            holder.tvName.setTextColor(Color.parseColor("#CF3333"));
        } else {
            holder.imgPlayTag.setVisibility(View.GONE);
            holder.tvName.setTextColor(Color.parseColor("#8888A0"));
        }
    }

    @Override
    public int getItemCount() {
        return channelList != null ? channelList.size() : 0;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnFocusChangeListener {

        private OnItemClickListener onItemClickListener;
        private OnItemFocusChangeListener onItemFocusChangeListener;
        private AppCompatImageView imgPlayTag;
        private TextView tvName;

        VH(@NonNull View itemView, OnItemClickListener itemClickListener,
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
//            if (playingPos != getLayoutPosition()) {
//                notifyItemChanged(playingPos);
//                playingPos = getLayoutPosition();
//                notifyItemChanged(playingPos);
//            }
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
