package com.anbetter.xplayer.exomedia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;

import com.anbetter.log.MLog;
import com.anbetter.xplayer.common.IXMediaPlayer;
import com.anbetter.xplayer.common.IXVideoView;
import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

/**
 * 对MP4格式的视频支持比较好，不支持直播流
 *
 * Created by android_ls on 2018/4/18.
 *
 * @author 红果果 sli@yoozoo.com
 * @version 1.0
 */
public class XVideoView extends FrameLayout implements IXVideoView {

    private VideoView mVideoView;
    private String mVideoPath;

    public XVideoView(@NonNull Context context) {
        super(context);
        setupViews(context);
    }

    public XVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupViews(context);
    }

    private void setupViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.exo_video_view_layout, this);
        mVideoView = (VideoView) findViewById(R.id.video_view);

        mVideoView.setMeasureBasedOnAspectRatioEnabled(true);
        mVideoView.setScaleType(ScaleType.CENTER);
    }

    @Override
    public void setVideoPath(String path) {
        mVideoPath = path;
        mVideoView.setVideoPath(path);
    }

    @Override
    public void setCoverView(@NonNull View view) {
//        mVideoView.setPreviewImage();
    }

    @Override
    public void setMediaPlayer(IXMediaPlayer mediaPlayer) {

    }

    @Override
    public void setDisplayAspectRatio(int displayAspectRatio) {

    }

    @Override
    public void setVideoRotation(int degree) {

    }

    @Override
    public void setLooping(boolean looping) {

    }

    @Override
    public int getCurrentState() {
        return 0;
    }

    @Override
    public void play() {
        // 在调用了mVideoView.stopPlayback()之后，再调用mVideoView.restart()返回true
        // 在调用了mVideoView.stopPlayback()之后，若不调用mVideoView.restart()方法，
        // 直接调用mVideoView.start()，视频是不会播放的
        boolean restart = mVideoView.restart();
        MLog.i("mVideoView.restart() = " + restart);
        mVideoView.start();
    }

    @Override
    public void resume() {
        if(!isPlaying()) {
            mVideoView.restart();
            mVideoView.start();
        }
    }

    @Override
    public void pause() {
        if(isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    public void stop() {
        if(isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void release() {
        mVideoView.release();
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void setNeedMute(boolean needMute) {

    }

    @Override
    public void seekTo(long pos) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public Surface getSurface() {
        return null;
    }

}
