package com.sdt.testthreeso.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sdt.testthreeso.ChannelManager;
import com.sdt.testthreeso.R;
import com.sdt.testthreeso.adapter.CategoryAdapter;
import com.sdt.testthreeso.adapter.ChannelAdapter;
import com.sdt.testthreeso.adapter.OnItemClickListener;
import com.sdt.testthreeso.adapter.OnItemFocusChangeListener;
import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.presenter.LivePresenter;

/**
 * @ClassName ChannelMenuView
 * @Description TODO
 * @Author Administrator
 * @Date 2021/3/18 10:06
 * @Version 1.0
 */
public class ChannelMenuView extends RelativeLayout {

    private LivePresenter livePresenter;

    private boolean frozenEvent = false;    //菜单显示时短暂时间内，不允许响应按键事件
    private static final String TAG = "ChannelMenuView";
    private TvRecyclerView rvCategory;
    private TvRecyclerView rvChannel;
    private CategoryAdapter mCategoryAdapter;
    private ChannelAdapter mChannelAdapter;

    private int mPlayingCategoryPosition;
    private int mPlayingChannelPosition;

    private int mFocusCategoryPosition;
    private int mFocusChannelPosition;

    private Category mPlayingCategory;
    private Channel mPlayingChannel;

    private int mLostFoucsPostion = -1;

    public ChannelMenuView(Context context) {
        this(context, null);
    }

    public ChannelMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public void injectPresenter(LivePresenter livePresenter) {
        this.livePresenter = livePresenter;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        if (rvCategory.hasFocus()) {
            if (direction == View.FOCUS_RIGHT) {
                if (mFocusCategoryPosition == mPlayingCategoryPosition) {
                    Log.d(TAG, "focusSearchX1");
                    View playingView = rvChannel.getLayoutManager().findViewByPosition(mPlayingChannelPosition);
                    if (playingView == null) {
                        return rvChannel.getLayoutManager().findViewByPosition(mFocusChannelPosition);
                    } else {
                        return playingView;
                    }
                } else {
                    Log.d(TAG, "focusSearchX2:" + mFocusChannelPosition);
                    return rvChannel.getLayoutManager().findViewByPosition(mFocusChannelPosition);
                }
            } else if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
                Log.d(TAG, "上下焦点搜索X1:" + mFocusChannelPosition);
                return rvCategory.getLayoutManager().findViewByPosition(mFocusCategoryPosition);
            }
        } else if (rvChannel.hasFocus()) {
            if (direction == View.FOCUS_LEFT) {
                if (mFocusCategoryPosition == mPlayingCategoryPosition) {
                    Log.d(TAG, "focusSearchX3" + mPlayingCategoryPosition);
                    View child = rvCategory.getLayoutManager().findViewByPosition(mPlayingCategoryPosition);
                    if (child != null) {
                        return child;
                    } else {
                        rvCategory.getLayoutManager().findViewByPosition(mFocusChannelPosition);
                    }
                } else {
                    Log.d(TAG, "focusSearchX4" + mFocusCategoryPosition);
                    return rvCategory.getLayoutManager().findViewByPosition(mFocusCategoryPosition);
                }
            } else if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
                Log.d(TAG, "上下焦点搜索X2:" + mFocusChannelPosition);
                return rvChannel.getLayoutManager().findViewByPosition(mFocusChannelPosition);
            }
        }
        return super.focusSearch(focused, direction);
    }

    public ChannelMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_channel_menu, this);
        rvCategory = rootView.findViewById(R.id.tv_recycler_category);
        rvChannel = rootView.findViewById(R.id.tv_recycler_channel);
        rvCategory.setLayoutManager(new FocusFixedLinearLayoutManager(getContext(), rvCategory));
        rvChannel.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void initData(Category playingCategory, Channel playingChannel) {
        mPlayingCategory = playingCategory;
        mPlayingChannel = playingChannel;
        mPlayingCategoryPosition = ChannelManager.getInstance().getCategoryIndex(mPlayingCategory);
        mPlayingChannelPosition = ChannelManager.getInstance().getChannelIndex(mPlayingCategory, mPlayingChannel);
        mCategoryAdapter = new CategoryAdapter(getContext(), ChannelManager.getInstance().getCategoryList());
        mCategoryAdapter.setPlayingPos(mPlayingCategoryPosition);
        rvCategory.setAdapter(mCategoryAdapter);

        mCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });

        mCategoryAdapter.setOnItemFocusChangeListener(new OnItemFocusChangeListener() {
            @Override
            public void onItemFocusChange(View view, boolean hasFocus, int position) {
                if (hasFocus) {
                    Log.i(TAG, mFocusCategoryPosition + ",CATEGORY GAN FOCUS:" + position);
                    if (mFocusCategoryPosition != position) {
                        mFocusCategoryPosition = position;
                        mChannelAdapter = new ChannelAdapter(getContext(), ChannelManager.getInstance().getCategoryList().get(mFocusCategoryPosition).getChannelList());
                        if (mFocusCategoryPosition == mPlayingCategoryPosition) {
                            mChannelAdapter.setPlayingPos(mPlayingChannelPosition);
                        }

                        mFocusChannelPosition = 0;
                        Log.d(TAG, "mFocusChannelPosition:" + mFocusChannelPosition);
                        rvChannel.setAdapter(mChannelAdapter);
                        mChannelAdapter.setOnItemClickListener(onItemClickListener);
                        mChannelAdapter.setOnItemFocusChangeListener(onItemFocusChangeListener);
                    }
                    Log.i(TAG, "category gan focus:" + position);
                } else {
                    Log.d(TAG, "CATEGORY LOSE FOCUS:" + position);
                }
            }
        });

        rvCategory.setOnLoseItemFocusListener(new TvRecyclerView.OnLoseItemFocusListener() {
            @Override
            public void onLoseItemFocus(View itemView, int direction, int position) {
                Log.d(TAG, "onLoseItemFocus:" + position + "," + direction);
                ((TextView) itemView.findViewById(R.id.tv_name)).setTextColor(Color.parseColor("#00FF00"));
                mLostFoucsPostion = position;
            }
        });

        mChannelAdapter = new ChannelAdapter(getContext(), mPlayingCategory.getChannelList());
        mChannelAdapter.setPlayingPos(mPlayingChannelPosition);
        rvChannel.setAdapter(mChannelAdapter);
        mChannelAdapter.setOnItemClickListener(onItemClickListener);
        mChannelAdapter.setOnItemFocusChangeListener(onItemFocusChangeListener);
        rvChannel.setOnLoseItemFocusListener(onLoseItemFocusListener);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d(TAG, "onItemClick:" + position);
            if (mPlayingCategoryPosition == mFocusCategoryPosition && mPlayingChannelPosition == position) {
                Log.d(TAG, "节目已经在播放中，RETURN");
                return;
            } else {
                if (mPlayingCategoryPosition != mFocusCategoryPosition) {
                    mCategoryAdapter.setPlayingPos(mFocusCategoryPosition);
                    mCategoryAdapter.notifyItemChanged(mPlayingCategoryPosition);
                    mPlayingCategoryPosition = mFocusCategoryPosition;
                    mCategoryAdapter.notifyItemChanged(mPlayingCategoryPosition);
                    mPlayingCategory = ChannelManager.getInstance().getCategoryList().get(mPlayingCategoryPosition);

                    mPlayingChannelPosition = position;
                    mChannelAdapter.setPlayingPos(mPlayingChannelPosition);
                    mChannelAdapter.notifyItemChanged(mPlayingChannelPosition);
                } else {
                    mChannelAdapter.setPlayingPos(position);
                    Log.d(TAG, "刷新旧的item" + mPlayingChannelPosition);
                    mChannelAdapter.notifyItemChanged(mPlayingChannelPosition);
                    mPlayingChannelPosition = position;
                    Log.i(TAG, "刷新新的item" + mPlayingChannelPosition);
                    mChannelAdapter.notifyItemChanged(mPlayingChannelPosition);
                }
                mPlayingChannel = mPlayingCategory.getChannelList().get(mPlayingChannelPosition);
                livePresenter.changeChannelByMenu(mPlayingCategory, mPlayingChannel);
            }

        }
    };

    private OnItemFocusChangeListener onItemFocusChangeListener = new OnItemFocusChangeListener() {

        @Override
        public void onItemFocusChange(View view, boolean hasFocus, int position) {
            if (hasFocus) {
                Log.i(TAG, "CHANNEL GAN FOCUS:" + position);
                mFocusChannelPosition = position;
            } else {
                Log.d(TAG, "CHANNEL LOSE FOCUS:" + position);
            }
        }
    };

    private TvRecyclerView.OnLoseItemFocusListener onLoseItemFocusListener = new TvRecyclerView.OnLoseItemFocusListener() {
        @Override
        public void onLoseItemFocus(View itemView, int direction, int position) {
            ((TextView) itemView.findViewById(R.id.tv_name)).setTextColor(Color.parseColor("#00FF00"));
        }
    };

    public void showMenu() {
        setVisibility(View.VISIBLE);
        frozenEvent = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                frozenEvent = false;
            }
        }, 600);
        View loseFocusChild = rvCategory.getLayoutManager().findViewByPosition(mLostFoucsPostion);
        if (loseFocusChild != null) {
            ((TextView) loseFocusChild.findViewById(R.id.tv_name)).setTextColor(Color.parseColor("#ACACAF"));
        } else {
            Log.d(TAG, "未搜索到lose Focus View:" + mLostFoucsPostion);
        }
        scrollTargetCategory();
    }


    private void scrollTargetCategory() {
        rvCategory.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvCategory.getLayoutManager().scrollToPosition(mPlayingCategoryPosition);
                rvCategory.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View categoryChildView = rvCategory.getLayoutManager().findViewByPosition(mPlayingCategoryPosition);
                        if (categoryChildView == null) {
                            Log.d(TAG, "未搜索到子View1" + mPlayingCategoryPosition);
                            scrollTargetCategory();
                        } else {
                            categoryChildView.requestFocus();
                            scrollTargetChannel();
                        }
                    }
                }, 150);

            }
        }, 50);
    }

    private void scrollTargetChannel() {
        rvChannel.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvChannel.getLayoutManager().smoothScrollToPosition(rvChannel, new RecyclerView.State(), mPlayingChannelPosition);
                View channelChildView = rvChannel.getLayoutManager().findViewByPosition(mPlayingChannelPosition);
                if (channelChildView == null) {
                    Log.d(TAG, "未搜索到子View2" + mPlayingChannelPosition);
                    scrollTargetChannel();
                } else {
                    channelChildView.requestFocus();
                }
            }
        }, 150);
    }

    public void hideMenu() {
        setVisibility(GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (frozenEvent) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            hideMenu();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    public void setPlayingChannel(Category category, Channel channel) {
        int oldCategoryIndex = mPlayingCategoryPosition;
        int oldChannelIndex = mPlayingChannelPosition;
        Log.d(TAG, "Old:" + oldCategoryIndex + "," + oldChannelIndex);
        if (oldCategoryIndex != mFocusCategoryPosition) {
            Log.d(TAG, "setPlayingChannel X1");
            //播放分类和焦点落上面的分类不一致
            mPlayingCategory = category;
            mPlayingChannel = channel;
            mPlayingCategoryPosition = ChannelManager.getInstance().getCategoryIndex(mPlayingCategory);
            mPlayingChannelPosition = ChannelManager.getInstance().getChannelIndex(mPlayingCategory, mPlayingChannel);
            Log.d(TAG, "New A:" + mPlayingCategoryPosition + "," + mPlayingChannelPosition);
            if (oldCategoryIndex != mPlayingCategoryPosition) {
                Log.d(TAG, "setPlayingChannel X2");
                mCategoryAdapter.setPlayingPos(mPlayingCategoryPosition);
                mCategoryAdapter.notifyItemChanged(oldCategoryIndex);
                mCategoryAdapter.notifyItemChanged(mPlayingCategoryPosition);
            } else {
                Log.d(TAG, "setPlayingChannel X3");
            }
        } else {
            mPlayingCategory = category;
            mPlayingChannel = channel;
            mPlayingCategoryPosition = ChannelManager.getInstance().getCategoryIndex(mPlayingCategory);
            mPlayingChannelPosition = ChannelManager.getInstance().getChannelIndex(mPlayingCategory, mPlayingChannel);
            Log.d(TAG, "New B:" + mPlayingCategoryPosition + "," + mPlayingChannelPosition);
            if (mPlayingCategoryPosition != oldCategoryIndex) {
                Log.d(TAG, "setPlayingChannel X4");
                //要播放的分类和正在播放的分类不一致
                mCategoryAdapter.setPlayingPos(mPlayingCategoryPosition);
                mCategoryAdapter.notifyItemChanged(oldCategoryIndex);
                mCategoryAdapter.notifyItemChanged(mPlayingCategoryPosition);
                scrollToPlayingCategory();
            } else {
                if (oldChannelIndex != mPlayingChannelPosition) {
                    Log.d(TAG, "setPlayingChannel X5");
                    mChannelAdapter.setPlayingPos(mPlayingChannelPosition);
                    mChannelAdapter.notifyItemChanged(oldChannelIndex);
                    mChannelAdapter.notifyItemChanged(mPlayingChannelPosition);
                    scrollToPlayingChannel();
                } else {
                    Log.d(TAG, "setPlayingChannel X6");
                    scrollToPlayingChannel();
                }
            }
        }
    }

    private void scrollToPlayingCategory() {
        rvCategory.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvCategory.getLayoutManager().scrollToPosition(mPlayingCategoryPosition);
                rvCategory.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View categoryChildView = rvCategory.getLayoutManager().findViewByPosition(mPlayingCategoryPosition);
                        if (categoryChildView == null) {
                            Log.d(TAG, "没有滚动到正在播放的分类:" + mPlayingCategoryPosition);
                            if (getVisibility() == VISIBLE) {
                                scrollToPlayingCategory();
                            }
                        } else {
                            //触发rvCategory Adapter onItemFocusChange 事件
                            //为了让频道分类自动刷新
                            categoryChildView.requestFocus();
                        }
                    }
                }, 150);

            }
        }, 10);
    }

    private void scrollToPlayingChannel() {
        rvChannel.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvChannel.getLayoutManager().scrollToPosition(mPlayingChannelPosition);
                rvChannel.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View focusedView = rvChannel.getLayoutManager().findViewByPosition(mPlayingChannelPosition);
                        if (focusedView == null) {
                            Log.d(TAG, "没有滚动到正在播放的频道:" + mPlayingChannelPosition);
                            if (getVisibility() == VISIBLE) {
                                scrollToPlayingChannel();
                            }
                        } else {
                            focusedView.requestFocus();
                        }
                    }
                }, 150);

            }
        }, 50);
    }
}
