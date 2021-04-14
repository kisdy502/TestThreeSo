package com.sdt.testthreeso.adapter;

import android.view.View;

/**
 * @ClassName OnItemFocusChangeListener
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 15:01
 * @Version 1.0
 */

public interface OnItemFocusChangeListener {
    void onItemFocusChange(View view, boolean hasFocus, int position); //该方法回调到activity或fragment中做相应操作
}
