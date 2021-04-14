package com.sdt.testthreeso.mvp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * @ClassName BaseMvpActivity
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/23 11:15
 * @Version 1.0
 */
public abstract class BaseMvpActivity extends AppCompatActivity {
    private MvpProxyImpl mvpProxy;
    protected List<BasePresenter> mPresenters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView;
        final Object layout = getLayout();
        if (layout instanceof Integer) {
            rootView = View.inflate(this, (int) getLayout(), null);
        } else if (layout instanceof View) {
            rootView = (View) getLayout();
        } else {
            throw new ClassCastException("type of setLayout() must be int or View!");
        }
        setContentView(rootView);
        mvpProxy = createProxy();
        mvpProxy.bindPresenter();
        afterCreate();
    }

    private MvpProxyImpl createProxy() {
        if (mvpProxy == null) {
            return new MvpProxyImpl<>((IBaseView) this);
        }
        return mvpProxy;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (BasePresenter p : mPresenters) {
            p.detachView();
        }
        mvpProxy.unbindPresenter();
        mPresenters.clear();
        mPresenters = null;
    }

    @SuppressWarnings("ConstantConditions")
    protected <T extends View> T $(@IdRes int viewId) {
        return findViewById(viewId);
    }

    public void showProgress(boolean isShow) {
        if (isShow) {
            Log.i("mvp_", "显示等待框");
        } else {
            Log.i("mvp_", "隐藏等待框");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected abstract Object getLayout();

    protected abstract void afterCreate();


}
