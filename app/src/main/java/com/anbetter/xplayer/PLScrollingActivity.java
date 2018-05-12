package com.anbetter.xplayer;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.anbetter.xplayer.common.IXRenderView;
import com.anbetter.xplayer.common.IXVideoView;
import com.anbetter.xplayer.pl.XDragVideoView;

/**
 * 大小视频窗口切换Demo
 * 实现思路：
 * <p>
 * Created by android_ls on 2018/4/28.
 *
 * @author android_ls
 * @version 1.0
 */
public class PLScrollingActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    public static final int APP_BAR_LAYOUT_IDLE = 0;
    public static final int APP_BAR_LAYOUT_EXPANDED = 1;
    public static final int APP_BAR_LAYOUT_COLLAPSED = 2;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private boolean isPause;
    private String videoUrl;
    private int mAppBarLayoutCurrentState = APP_BAR_LAYOUT_IDLE;

    private IXVideoView mVideoView;
    private XDragVideoView mSmallVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_pl);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mCollapsingToolbarLayout.setTitle("XPlayer");

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        videoUrl = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";

        // 小窗口
        mSmallVideoView = findViewById(R.id.drag_video_view);
        mSmallVideoView.setDisplayAspectRatio(IXRenderView.AR_MATCH_PARENT);
        mSmallVideoView.setVideoPath(videoUrl);
        mSmallVideoView.setLooping(false);

        // 大窗口
        mVideoView = findViewById(R.id.video_view);
        mVideoView.setDisplayAspectRatio(IXRenderView.AR_MATCH_PARENT);
        mVideoView.setVideoPath(videoUrl);
        mVideoView.setLooping(false);
        mVideoView.play();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        MLog.i("verticalOffset = " + verticalOffset);
        if (verticalOffset == 0) {
            if (mAppBarLayoutCurrentState != APP_BAR_LAYOUT_EXPANDED) {
                mCollapsingToolbarLayout.setTitle("");

                if(!mVideoView.isPlaying()) {
                    mVideoView.play();
                }

                if (mSmallVideoView.getVisibility() == View.VISIBLE) {
                    mSmallVideoView.setVisibility(View.GONE);

                    if(mSmallVideoView.isPlaying()) {
                        mSmallVideoView.pause();
                    }
                }

                mAppBarLayoutCurrentState = APP_BAR_LAYOUT_EXPANDED;
            }
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mAppBarLayoutCurrentState != APP_BAR_LAYOUT_COLLAPSED) {
                mCollapsingToolbarLayout.setTitle("大小视频显示组件互动");

                if(mVideoView.isPlaying()) {
                    mVideoView.pause();
                }

                if (mSmallVideoView.getVisibility() == View.GONE) {
                    mSmallVideoView.setVisibility(View.VISIBLE);
                    // 重置到初始化所在位置
                    mSmallVideoView.restorePosition();

                    if(!mSmallVideoView.isPlaying()) {
                        mSmallVideoView.play();
                    }
                }

                mAppBarLayoutCurrentState = APP_BAR_LAYOUT_COLLAPSED;
            }
        } else {
            if (mAppBarLayoutCurrentState != APP_BAR_LAYOUT_IDLE) {
                if (mAppBarLayoutCurrentState == APP_BAR_LAYOUT_COLLAPSED) {
                    // 中间状态
                    mSmallVideoView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!mVideoView.isPlaying()) {
                                mVideoView.play();
                            }

                            if (mSmallVideoView.getVisibility() == View.VISIBLE) {
                                mSmallVideoView.setVisibility(View.GONE);

                                if(mSmallVideoView.isPlaying()) {
                                    mSmallVideoView.pause();
                                }
                            }
                        }
                    }, 50);
                }
                mAppBarLayoutCurrentState = APP_BAR_LAYOUT_IDLE;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mSmallVideoView.isPlaying()) {
            mSmallVideoView.pause();
            isPause = true;
        } else {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                isPause = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPause) {
            return;
        }

        if(!mSmallVideoView.isPlaying()) {
            mSmallVideoView.play();
        } else {
            if(!mVideoView.isPlaying()) {
                mVideoView.play();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mSmallVideoView.isPlaying()) {
            mSmallVideoView.release();
        }

        if(mVideoView.isPlaying()) {
            mVideoView.release();
        }
        super.onDestroy();
    }

}
