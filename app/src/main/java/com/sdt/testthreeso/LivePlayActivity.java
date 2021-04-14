package com.sdt.testthreeso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ffmpeg.FFMPEGRenderFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.sdt.testthreeso.bean.Category;
import com.sdt.testthreeso.bean.Channel;
import com.sdt.testthreeso.bean.LiveSource;
import com.sdt.testthreeso.presenter.LivePresenter;
import com.sdt.testthreeso.utils.Constants;
import com.sdt.testthreeso.utils.LogWriter;
import com.sdt.testthreeso.widget.ChannelMenuView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 直播Activity
 */
public class LivePlayActivity extends AppCompatActivity implements ILiveView {

    private LivePresenter livePresenter;
    private final static String TAG = "LivePlayActivity";
    private PlayerView playerView;
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    private boolean startAutoPlay = true;
    private int startWindow;
    private long startPosition;

    private String playUrl;
    private TextView tvDebugInfo;
    private TextView tvStateInfo;
    private ChannelMenuView channelMenuView;
    private DataChangedReceiver receiver;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_play);
        playerView = findViewById(R.id.play_view);
        tvDebugInfo = findViewById(R.id.tv_debug_info);
        tvStateInfo = findViewById(R.id.tv_state_info);
        channelMenuView = findViewById(R.id.layout_channel_menu);
        initExoPlayer();
        livePresenter = new LivePresenter(this);
        channelMenuView.injectPresenter(livePresenter);
        livePresenter.loadCategoryAndChannel();

        receiver = new DataChangedReceiver();
        IntentFilter filter1 = new IntentFilter(Constants.ACTION_CHANGE_CATEGORY);
        IntentFilter filter2 = new IntentFilter(Constants.ACTION_CHANGE_CHANNEL);
        IntentFilter filter3 = new IntentFilter(Constants.ACTION_CHANGE_SOURCE);
        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        registerReceiver(receiver, filter3);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        clearStartPosition();
        releasePlayer();
        initExoPlayer();
    }

    private void showDebugInfo(Category category, Channel channel, LiveSource liveSource) {
        tvDebugInfo.setText("");
        tvDebugInfo.append("categoryId:" + category.getId());
        tvDebugInfo.append("     categoryName:" + category.getName());
        tvDebugInfo.append("     channelId:" + channel.getChannelId());
        tvDebugInfo.append("     channelName:" + channel.getName());
        tvDebugInfo.append("\nurl:" + playUrl);
    }

    private void initExoPlayer() {
        dataSourceFactory = App.getInstance().getDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        FFMPEGRenderFactory renderersFactory = new FFMPEGRenderFactory(getApplicationContext());
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder(getApplicationContext()).build();
        trackSelector = new DefaultTrackSelector(getApplicationContext());
        trackSelector.setParameters(trackSelectorParameters);

        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.setUseController(false);
        //playerView.requestFocus();
        player = new SimpleExoPlayer.Builder(getApplicationContext(), renderersFactory)
                .setTrackSelector(trackSelector)
                .build();
        player.addListener(new PlayerEventListener());
        player.setPlayWhenReady(startAutoPlay);
        player.addAnalyticsListener(new EventLogger(trackSelector));
        playerView.setPlayer(player);
        playerView.setControlDispatcher(new DefaultControlDispatcher());
    }

    private void initUrlAndPlay() {
        mediaSource = buildMediaSource(playUrl, null);
        player.setMediaSource(mediaSource);
        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.prepare();
    }

    private MediaSource buildMediaSource(String playUrl, String overrideExtension) {
        Uri uri = Uri.parse(playUrl);
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        Log.d(TAG, "url type:" + type + "," + playUrl);
        switch (type) {

            case C.TYPE_HLS:
                HlsMediaSource hlsMediaSource =
                        new HlsMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(MediaItem.fromUri(uri));
                return hlsMediaSource;
            case C.TYPE_OTHER:
                ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(uri));
                return progressiveMediaSource;
            case C.TYPE_DASH:
//                DashMediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(MediaItem.fromUri(uri));
//                return dashMediaSource;
                Log.e(TAG, "Not support dash source");
            case C.TYPE_SS:
//                MediaSource ssMediaSource =
//                        new SsMediaSource.Factory(dataSourceFactory)
//                                .createMediaSource(MediaItem.fromUri(uri));
//                return ssMediaSource;
                Log.e(TAG, "Not support smoothstreaming source");
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    protected void releasePlayer() {
        if (player != null) {
            if (trackSelector != null) {
                trackSelectorParameters = trackSelector.getParameters();
            }
            updateStartPosition();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (channelMenuView.getVisibility() == View.VISIBLE) {
            return channelMenuView.dispatchKeyEvent(event);
        }
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            livePresenter.changeNextSource();
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            livePresenter.changeNextChannel();
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            channelMenuView.showMenu();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        unregisterReceiver(receiver);
    }


    private class PlayerEventListener implements Player.EventListener {
        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (isBehindLiveWindow(error)) {
                // Re-initialize player at the current live window default position.
                player.seekToDefaultPosition();
                player.prepare();
            } else if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                error.getSourceException().printStackTrace();
                Toast.makeText(getApplicationContext(), "播放失败,3秒后切下一个源", Toast.LENGTH_SHORT).show();
                delayToNext(3);
            } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = error.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    Toast.makeText(getApplicationContext(), "渲染视频出错", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            if (playbackState == Player.STATE_READY) {
                tvStateInfo.setText("播放状态：正在播放");
                Log.d(TAG, "isCurrentWindowLive:" + player.isCurrentWindowLive());
                Log.d(TAG, "isCurrentWindowDynamic:" + player.isCurrentWindowDynamic());
                LogWriter.writeText(getApplicationContext(), "Success:" + playUrl);
                //播放测试时，自动切下一个
//                Toast.makeText(getApplicationContext(), "播放成功,30秒切下一个源", Toast.LENGTH_SHORT).show();
//                delayToNext(30);
            } else if (playbackState == Player.STATE_BUFFERING) {
                tvStateInfo.setText("播放状态：缓冲中");
            } else if (playbackState == Player.STATE_IDLE) {
                tvStateInfo.setText("播放状态：空闲状态");
            } else if (playbackState == Player.STATE_ENDED) {
                tvStateInfo.setText("播放状态：播放结束");
            }
        }
    }

    private void delayToNext(int delay) {
        Observable.just(1).delay(delay, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                        livePresenter.changeNextSource();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }


    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getApplicationContext().getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getApplicationContext().getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getApplicationContext().getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getApplicationContext().getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getApplicationContext().getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo.name);
                    }
                }
                Log.d(TAG, "errorString:" + errorString);
            }
            return Pair.create(0, errorString);
        }
    }

    private class DataChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(Constants.ACTION_CHANGE_CATEGORY)) {
                livePresenter.changeNextCategory();
            } else if (action.equalsIgnoreCase(Constants.ACTION_CHANGE_CHANNEL)) {
                livePresenter.changeNextChannel();
            } else if (action.equalsIgnoreCase(Constants.ACTION_CHANGE_SOURCE)) {
                livePresenter.changeNextSource();
            }
        }
    }

    @Override
    public void realPlay(Category category, Channel channel, LiveSource liveSource, boolean refreshMenu) {
        playUrl = liveSource.getUrl();
        showDebugInfo(category, channel, liveSource);
        initUrlAndPlay();
        if (refreshMenu) {
            channelMenuView.setPlayingChannel(category, channel);
        }
    }

    @Override
    public void initData(Category playingCategory, Channel playingChannel) {
        channelMenuView.initData(playingCategory, playingChannel);
    }
}