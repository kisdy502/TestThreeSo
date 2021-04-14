package com.sdt.testthreeso.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 长按下键焦点乱飞bug处理
 * 该LayoutManager仅处理了垂直方向滚动的问题
 */
public class FocusFixedLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = "FixedLayoutManager";
    private RecyclerView recyclerView;

    public FocusFixedLinearLayoutManager(Context context, RecyclerView recyclerView) {
        super(context);
        this.recyclerView = recyclerView;
    }

    public FocusFixedLinearLayoutManager(Context context) {
        super(context);
    }

    public FocusFixedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FocusFixedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
        if (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP) {
            View view = getFocusedChild();
            int fromPos = getPosition(view);
            int count = getItemCount();
            int last = findLastVisibleItemPosition();
            Log.d(TAG, "fromPos:" + fromPos + ",last:" + last);
            switch (direction) {
                case View.FOCUS_DOWN:
                    ++fromPos;
                    break;
                case View.FOCUS_UP:
                    --fromPos;
                    break;
            }
            if (fromPos < 0 || fromPos > count) {
                return focused;
            } else {
                if (fromPos > last) {
//                    smoothScrollToPosition(recyclerView, new RecyclerView.State(), currentPosition);
                    scrollToPosition(fromPos);
                }
            }

        }
        return super.onInterceptFocusSearch(focused, direction);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    // 返回：滑过1px时经历的时间(ms)。
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        //super.calculateSpeedPerPixel(displayMetrics);
                        return 50F / displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }


}



