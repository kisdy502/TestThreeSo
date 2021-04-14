package com.sdt.testthreeso;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.device.DeviceInfo;
import com.google.android.exoplayer2.device.DeviceListener;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.sdt.testthreeso.utils.DemoUtil;
import com.sdt.testthreeso.utils.LogWriter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";
    protected SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private SurfaceView surfaceView;
    private String m3u8Url = "https://38649.live-vod.cdn.aodianyun.com/clip/0x0/1d970d01e60ccaaa8ae85811fdcfb014/1d970d01e60ccaaa8ae85811fdcfb014.m3u8";
    private MediaItem mediaItem;
    private ProgressBar progressBar;
    private DataSource.Factory dataSourceFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        m3u8Url = getIntent().getStringExtra("playUrl");
        mediaItem = MediaItem.fromUri(m3u8Url);
        Log.d(TAG, "onCreate,m3u8Url:" + m3u8Url);
        surfaceView = findViewById(R.id.player_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                startAutoPlay = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed");
                startAutoPlay = false;
            }
        });
        progressBar = findViewById(R.id.progress_bar);
        setTitle(m3u8Url);
        DefaultTrackSelector.ParametersBuilder builder =
                new DefaultTrackSelector.ParametersBuilder(/* context= */ this);
        trackSelectorParameters = builder.build();
        dataSourceFactory = DemoUtil.getDataSourceFactory(/* context= */ this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        m3u8Url = intent.getStringExtra("playUrl");
        Log.d(TAG, "onNewIntent,m3u8Url:" + m3u8Url);
        mediaItem = MediaItem.fromUri(m3u8Url);
        setIntent(intent);
        setTitle(m3u8Url);
        releasePlayer();
        initializePlayer();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            startAutoPlay = true;
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            startAutoPlay = true;
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializePlayer() {


        boolean preferExtensionDecoders = false;
        RenderersFactory renderersFactory =
                DemoUtil.buildRenderersFactory(/* context= */ this, preferExtensionDecoders);

        trackSelector = new DefaultTrackSelector(/* context= */ this);
        trackSelector.setParameters(trackSelectorParameters);

        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(dataSourceFactory);

        player = new SimpleExoPlayer.Builder(/* context= */ this, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .build();
        player.addListener(new PlayerEventListener());
        player.addAnalyticsListener(new EventLogger(trackSelector));
        player.addTextOutput(new TextOutput() {
            @Override
            public void onCues(List<Cue> cues) {
                Log.d(TAG, "字幕:" + cues.size());
            }
        });
        player.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.d(TAG, "onVideoSizeChanged:" + width + "," + height);
                Log.d(TAG, "onVideoSizeChanged:" + unappliedRotationDegrees + "," + pixelWidthHeightRatio);
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Log.d(TAG, "onSurfaceSizeChanged:" + width + "," + height);
            }

            @Override
            public void onRenderedFirstFrame() {
                Log.d(TAG, "首帧加载!");
            }
        });
        player.addAudioListener(new AudioListener() {
            @Override
            public void onAudioSessionIdChanged(int audioSessionId) {
                Log.d(TAG, "onAudioSessionIdChanged!" + audioSessionId);
            }

            @Override
            public void onVolumeChanged(float volume) {
                Log.d(TAG, "onVolumeChanged!" + volume);
            }
        });
        player.addDeviceListener(new DeviceListener() {
            @Override
            public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
                Log.d(TAG, "onDeviceInfoChanged!" + deviceInfo.maxVolume);
                Log.d(TAG, "onDeviceInfoChanged!" + deviceInfo.minVolume);
            }

            @Override
            public void onDeviceVolumeChanged(int volume, boolean muted) {
                Log.d(TAG, "onDeviceVolumeChanged!" + volume + "," + muted);
            }
        });

        player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
        player.setVideoSurfaceView(surfaceView);
        player.setPlayWhenReady(startAutoPlay);

        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            player.seekTo(startWindow, startPosition);
        }
        player.setMediaItem(mediaItem, /* resetPosition= */ !haveStartPosition);
        //player.setMediaSource(hlsMediaSource, /* resetPosition= */ !haveStartPosition);
        player.prepare();
    }

    protected void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            Log.d(TAG, "onPlaybackStateChanged:" + playbackState);
            if (playbackState == Player.STATE_ENDED) {
                progressBar.setVisibility(View.GONE);
            } else if (playbackState == Player.STATE_READY) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "isCurrentWindowLive:" + player.isCurrentWindowLive());
                Log.d(TAG, "isCurrentWindowDynamic:" + player.isCurrentWindowDynamic());
                LogWriter.writeText(getApplicationContext(), "Success:" + m3u8Url);
                Observable.just(1).delay(10, TimeUnit.SECONDS).subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext:" + integer);
                        sendBroadcast(new Intent("com.sdt.Intent.CHANGE_NEXT"));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPlayerError(@NonNull ExoPlaybackException error) {
            Log.d(TAG, "onPlayerError:", error);
            Log.d(TAG, "error_type:" + error.type);
            if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                IOException cause = error.getSourceException();
                if (cause instanceof HttpDataSource.HttpDataSourceException) {
                    // An HTTP error occurred.
                    HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                    // This is the request for which the error occurred.
                    DataSpec requestDataSpec = httpError.dataSpec;
                    Log.e(TAG, "onPlayerError:" + requestDataSpec.uri.toString());
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                        Log.e(TAG, "onPlayerError:" + ((HttpDataSource.InvalidResponseCodeException) httpError).responseCode);
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.
                    }
                } else if (cause instanceof SocketTimeoutException) {
                    LogWriter.writeText(getApplicationContext(), "SocketTimeout Source:" + m3u8Url);
                }
                LogWriter.writeText(getApplicationContext(), "Fail:" + m3u8Url);
            } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = error.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    Toast.makeText(getApplicationContext(), "渲染视频出错", Toast.LENGTH_SHORT).show();
                }
                LogWriter.writeText(getApplicationContext(), "RENDERER FAILED:" + m3u8Url);
            } else if (isBehindLiveWindow(error)) {
                // Re-initialize player at the current live window default position.
                player.seekToDefaultPosition();
                player.prepare();
            } else {
                // Handle other errors.
                Toast.makeText(getApplicationContext(), "未知错误:" + error.type, Toast.LENGTH_SHORT).show();
            }
            sendBroadcast(new Intent("com.sdt.Intent.CHANGE_NEXT"));
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(
                @NonNull TrackGroupArray trackGroups, @NonNull TrackSelectionArray trackSelections) {
            Log.d(TAG, "onTracksChanged:");
        }

        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Object manifest = player.getCurrentManifest();
            if (manifest != null) {
                HlsManifest hlsManifest = (HlsManifest) manifest;
                // Do something with the manifest.
                Log.d(TAG, "baseUri:" + hlsManifest.mediaPlaylist.baseUri);
                List<Uri> uriList = hlsManifest.masterPlaylist.mediaPlaylistUrls;
                List<HlsMediaPlaylist.Segment> segmentList = hlsManifest.mediaPlaylist.segments;
                for (HlsMediaPlaylist.Segment segment : segmentList) {
                    Log.d(TAG, "uri:" + segment.url);
                }
            }

        }
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

}