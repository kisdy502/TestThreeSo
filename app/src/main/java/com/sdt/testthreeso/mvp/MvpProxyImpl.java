package com.sdt.testthreeso.mvp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MvpProxyImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/23 11:16
 * @Version 1.0
 */
public class MvpProxyImpl<V extends IBaseView> implements IMvpProxy {

    private V mView;
    private List<BasePresenter> mPresenters;

    public MvpProxyImpl(V view) {
        this.mView = view;
        mPresenters = new ArrayList<>();
    }

    @Override
    public void bindPresenter() {
        Field[] fields = mView.getClass().getDeclaredFields();
        for (Field field : fields) {
            //获取变量上面的注解类型
            InjectPresenter injectPresenter = field.getAnnotation(InjectPresenter.class);
            if (injectPresenter != null) {
                try {
                    Class<? extends BasePresenter> type = (Class<? extends BasePresenter>) field.getType();
                    BasePresenter mInjectPresenter = type.newInstance();
                    mInjectPresenter.attachView(mView);
                    field.setAccessible(true);
                    field.set(mView, mInjectPresenter);
                    mPresenters.add(mInjectPresenter);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    throw new RuntimeException("SubClass must extends Class:BasePresenter");
                }
            }
        }


    }

    @Override
    public void unbindPresenter() {
        for (BasePresenter presenter : mPresenters) {
            presenter.detachView();
        }
        mPresenters.clear();
        mPresenters = null;

    }
}
