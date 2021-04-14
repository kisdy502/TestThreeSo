package com.sdt.testthreeso.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @ClassName BaseMvpFragment
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/23 11:27
 * @Version 1.0
 */
public abstract class BaseMvpFragment extends Fragment {
    private MvpProxyImpl mvpProxy;
    public Activity mActivity;
    private View mRootView;

    protected abstract @LayoutRes
    int getLayoutId();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @SuppressWarnings("ConstantConditions")
    protected <T extends View> T $(@IdRes int viewId) {
        return this.getView().findViewById(viewId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            try {
                mRootView = inflater.inflate(getLayoutId(), container, false);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("根布局的id非法导致根布局为空,请检查后重试!");
        }
        mvpProxy = createProxy();
        mvpProxy.bindPresenter();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterCreate();
    }

    private MvpProxyImpl createProxy() {
        if (mvpProxy == null) {
            return new MvpProxyImpl<>((IBaseView) this);
        }
        return mvpProxy;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mvpProxy.unbindPresenter();
    }

    protected abstract void afterCreate();
}


