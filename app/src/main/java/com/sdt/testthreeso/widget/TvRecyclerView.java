package com.sdt.testthreeso.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ClassName TvRecyclerView
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 11:17
 * @Version 1.0
 */
public class TvRecyclerView extends RecyclerView {

    public TvRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TvRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 当不可见时，滚动
    public void scrollIfFocusViewNotVisible(int targetPosition) {
        if (getLayoutManager() == null || !(getLayoutManager() instanceof LinearLayoutManager)) {
            return;
        }
        int last = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int first = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        if (targetPosition > last) {
            getLayoutManager().smoothScrollToPosition(this, new RecyclerView.State(), targetPosition);
        } else if (targetPosition < first) {
            getLayoutManager().smoothScrollToPosition(this, new RecyclerView.State(), targetPosition);
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }
        if (view != null) {
            //findContainingItemView()方法返回焦点view所在的父view,
            //如果是在recyclerview之外，就会是null
            //所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                int focusPosition = getChildViewHolder(focused).getAdapterPosition();
                if (onLoseItemFocusListener != null) {
                    onLoseItemFocusListener.onLoseItemFocus(focused, direction, focusPosition);
                }
                return view;
            }
        }
        return view;
    }

    private OnLoseItemFocusListener onLoseItemFocusListener;

    public void setOnLoseItemFocusListener(OnLoseItemFocusListener onLoseItemFocusListener) {
        this.onLoseItemFocusListener = onLoseItemFocusListener;
    }

    /**
     * 焦点飞出RecyclerView时，记录最后获得焦点的子View
     * 用途菜单高亮显示之类的功能
     */
    public interface OnLoseItemFocusListener {
        void onLoseItemFocus(View itemView, int direction, int position);
    }
}
