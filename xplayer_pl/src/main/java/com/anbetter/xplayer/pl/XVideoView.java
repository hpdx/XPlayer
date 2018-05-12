package com.anbetter.xplayer.pl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;

import com.anbetter.log.MLog;
import com.anbetter.xplayer.common.listener.IXVideoViewListener;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLOnBufferingUpdateListener;
import com.pili.pldroid.player.PLOnCompletionListener;
import com.pili.pldroid.player.PLOnErrorListener;
import com.pili.pldroid.player.PLOnInfoListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;
import com.pili.pldroid.player.PlayerState;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;
import com.anbetter.xplayer.common.IXMediaPlayer;
import com.anbetter.xplayer.common.IXRenderView;
import com.anbetter.xplayer.common.IXVideoView;

/**
 * 基于PLVideoTextureView的视频播放器组件
 * <p>
 * Created by android_ls on 2018/4/28.
 *
 * @author android_ls
 * @version 1.0
 */
public class XVideoView extends FrameLayout implements IXVideoView, PLOnInfoListener,
        PLOnErrorListener, PLOnCompletionListener, PLOnBufferingUpdateListener, PLOnVideoSizeChangedListener {

    private static final String TAG = XVideoView.class.getSimpleName();

    private PLVideoTextureView mVideoView;

    public XVideoView(@NonNull Context context) {
        super(context);
        setupViews(context);
    }

    public XVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    private void setupViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pl_video_view_layout, this);
        mVideoView = findViewById(R.id.video_texture_view);

        AVOptions options = new AVOptions();
        // 解码方式，codec＝1，硬解; codec=0, 软解
        // 默认值是：0
        options.setInteger(AVOptions.KEY_MEDIACODEC, 0);
        // 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
        // 默认值是：无
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
        // 默认值是：0
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        mVideoView.setAVOptions(options);

        mVideoView.setOnInfoListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
    }

    @Override
    public void setVideoPath(@NonNull String path) {
        mVideoView.setVideoPath(path);
    }

    @Override
    public void setCoverView(@NonNull View view) {
        mVideoView.setCoverView(view);
    }

    @Override
    public void setMediaPlayer(IXMediaPlayer mediaPlayer) {

    }

    @Override
    public void setDisplayAspectRatio(int displayAspectRatio) {
        if (displayAspectRatio == IXRenderView.AR_ASPECT_FIT_PARENT) {
            // 水平方向拉伸填满屏幕，竖直方向居中
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);
        } else if (displayAspectRatio == IXRenderView.AR_MATCH_PARENT) {
            // 拉伸到填充满指定尺寸
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        } else if (displayAspectRatio == IXRenderView.AR_ASPECT_WRAP_CONTENT) {
            // 保持视频内容的原始尺寸
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_ORIGIN);
        } else if (displayAspectRatio == IXRenderView.AR_16_9_FIT_PARENT) {
            // 宽高比16:9，宽是填充满指定的尺寸的
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_16_9);
        } else if (displayAspectRatio == IXRenderView.AR_4_3_FIT_PARENT) {
            // 宽高比4:3，宽是填充满指定的尺寸的
            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_4_3);
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mVideoView.setRotation(degree);
    }

    @Override
    public void setLooping(boolean looping) {
        mVideoView.setLooping(looping);
    }

    @Override
    public void seekTo(long pos) {
        if (isPlaying()) {
            mVideoView.seekTo(pos);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (isPlaying()) {
            mVideoView.setPlaySpeed(speed);
        }
    }

    @Override
    public void setVideoViewListener(IXVideoViewListener listener) {

    }

    @Override
    public Surface getSurface() {
        if (mVideoView != null && mVideoView.getTextureView() != null) {
            return new Surface(mVideoView.getTextureView().getSurfaceTexture());
        }
        return null;
    }

    @Override
    public void setNeedMute(boolean needMute) {
        if (isPlaying()) {
            if (needMute) {
                mVideoView.setVolume(0, 0);
            } else {
                mVideoView.setVolume(1, 1);
            }
        }
    }

    @Override
    public int getCurrentState() {
        if (mVideoView != null) {
            int currentState = 0;
            PlayerState playerState = mVideoView.getPlayerState();
            if (playerState == PlayerState.IDLE) {
                currentState = IXMediaPlayer.STATE_IDLE;
            } else if (playerState == PlayerState.PREPARING) {
                currentState = IXMediaPlayer.STATE_PREPARING;
            } else if (playerState == PlayerState.PREPARED) {
                currentState = IXMediaPlayer.STATE_PREPARED;
            } else if (playerState == PlayerState.PLAYING) {
                currentState = IXMediaPlayer.STATE_PLAYING;
            } else if (playerState == PlayerState.PAUSED) {
                currentState = IXMediaPlayer.STATE_PAUSED;
            } else if (playerState == PlayerState.COMPLETED) {
                currentState = IXMediaPlayer.STATE_COMPLETED;
            } else if (playerState == PlayerState.DESTROYED) {
                currentState = IXMediaPlayer.STATE_IDLE;
            } else if (playerState == PlayerState.ERROR) {
                currentState = IXMediaPlayer.STATE_ERROR;
            }
            return currentState;
        }
        return 0;
    }

    @Override
    public void play() {
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    public void pause() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    public void resume() {
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    public void stop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void release() {
        stop();
    }

    @Override
    public boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    @Override
    public int getDuration() {
        if (mVideoView != null) {
            return (int) mVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mVideoView != null) {
            return (int) mVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onInfo(int what, int extra) {
        MLog.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
        switch (what) {
            case MEDIA_INFO_BUFFERING_START:
                break;
            case MEDIA_INFO_BUFFERING_END:
                break;
            case MEDIA_INFO_VIDEO_RENDERING_START:
                MLog.i(TAG, "First video render time: " + extra + "ms");
                break;
            case MEDIA_INFO_AUDIO_RENDERING_START:
                MLog.i(TAG, "First audio render time: " + extra + "ms");
                break;
            case MEDIA_INFO_VIDEO_FRAME_RENDERING:
                MLog.i(TAG, "video frame rendering, ts = " + extra);
                break;
            case MEDIA_INFO_AUDIO_FRAME_RENDERING:
                MLog.i(TAG, "audio frame rendering, ts = " + extra);
                break;
            case MEDIA_INFO_VIDEO_GOP_TIME:
                MLog.i(TAG, "Gop Time: " + extra);
                break;
            case MEDIA_INFO_SWITCHING_SW_DECODE:
                MLog.i(TAG, "Hardware decoding failure, switching software decoding!");
                break;
            case MEDIA_INFO_METADATA:
                MLog.i(TAG, mVideoView.getMetadata().toString());
                break;
            case MEDIA_INFO_VIDEO_BITRATE:
            case MEDIA_INFO_VIDEO_FPS:

                break;
            case MEDIA_INFO_CONNECTED:
                MLog.i(TAG, "Connected !");
                break;
            case MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                MLog.i(TAG, "Rotation changed: " + extra);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(int errorCode) {
        Log.e(TAG, "Error happened, errorCode = " + errorCode);
        switch (errorCode) {
            case ERROR_CODE_IO_ERROR:
                /**
                 * SDK will do reconnecting automatically
                 */
                MLog.i(TAG, "IO Error !");
                return false;
            case ERROR_CODE_OPEN_FAILED:
                MLog.i(TAG, "failed to open player !");
                break;
            case ERROR_CODE_SEEK_FAILED:
                MLog.i(TAG, "failed to seek !");
                break;
            default:
                MLog.i(TAG, "unknown error !");
                break;
        }
        return true;
    }

    @Override
    public void onCompletion() {
        MLog.i(TAG, "Play Completed !");

    }

    @Override
    public void onBufferingUpdate(int precent) {
        MLog.i(TAG, "onBufferingUpdate: " + precent);
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        MLog.i(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);

    }

}
