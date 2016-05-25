package com.android.tedcoder.androidvideoplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.tedcoder.wkvideoplayer.dlna.engine.DLNAContainer;
import com.android.tedcoder.wkvideoplayer.dlna.service.DLNAService;
import com.android.tedcoder.wkvideoplayer.model.Video;
import com.android.tedcoder.wkvideoplayer.model.VideoUrl;
import com.android.tedcoder.wkvideoplayer.util.DensityUtil;
import com.android.tedcoder.wkvideoplayer.view.MediaController;
import com.android.tedcoder.wkvideoplayer.view.SuperVideoPlayer;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int ACTION_FILE_EXPLORER = 1000;
    private String filePath = "";
    private SuperVideoPlayer mSuperVideoPlayer;
    private SuperVideoPlayer mSuperVideoPlayer2;
    private View mPlayBtnView;

    private ImageView file_choose;

    private LinearLayout layout_video;

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            mSuperVideoPlayer.close();
            mSuperVideoPlayer2.close();
            mPlayBtnView.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.setVisibility(View.GONE);
            mSuperVideoPlayer2.setVisibility(View.GONE);
            resetPageToPortrait();
        }

        @Override
        public void onSwitchPageType() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
                mSuperVideoPlayer2.setPageType(MediaController.PageType.SHRINK);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
                mSuperVideoPlayer2.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_choose_mv:
                Intent i = new Intent(MainActivity.this, FileExplore.class);
                MainActivity.this.startActivityForResult(i, ACTION_FILE_EXPLORER);
                break;
            case R.id.play_btn:
                mPlayBtnView.setVisibility(View.GONE);
                mSuperVideoPlayer.setVisibility(View.VISIBLE);
                mSuperVideoPlayer2.setVisibility(View.VISIBLE);
                mSuperVideoPlayer.setAutoHideController(false);
                mSuperVideoPlayer2.setAutoHideController(false);

                Video video = new Video();
                VideoUrl videoUrl1 = new VideoUrl();
                videoUrl1.setFormatName("720P");
                videoUrl1.setFormatUrl(filePath);
                VideoUrl videoUrl2 = new VideoUrl();
                videoUrl2.setFormatName("480P");
                videoUrl2.setFormatUrl(filePath);
                ArrayList<VideoUrl> arrayList1 = new ArrayList<>();
                arrayList1.add(videoUrl1);
                arrayList1.add(videoUrl2);
                video.setVideoName("测试视频一");
                video.setVideoUrl(arrayList1);

                Video video2 = new Video();
                VideoUrl videoUrl3 = new VideoUrl();
                videoUrl3.setFormatName("720P");
                videoUrl3.setFormatUrl(filePath);
                VideoUrl videoUrl4 = new VideoUrl();
                videoUrl4.setFormatName("480P");
                videoUrl4.setFormatUrl(filePath);
                ArrayList<VideoUrl> arrayList2 = new ArrayList<>();
                arrayList2.add(videoUrl3);
                arrayList2.add(videoUrl4);
                video2.setVideoName("测试视频二");
                video2.setVideoUrl(arrayList2);

                ArrayList<Video> videoArrayList = new ArrayList<>();
                videoArrayList.add(video);
                videoArrayList.add(video2);

                mSuperVideoPlayer.loadMultipleVideo(videoArrayList, 0, 0, 0);
                mSuperVideoPlayer2.loadMultipleVideo(videoArrayList, 0, 0, 0);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDLNAService();
    }

    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == mSuperVideoPlayer) return;
        if (null == mSuperVideoPlayer2) return;
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSuperVideoPlayer.getLayoutParams().height = (int) width;
            mSuperVideoPlayer2.getLayoutParams().height = (int) width;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;
            mSuperVideoPlayer2.getLayoutParams().width = (int) height;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            mSuperVideoPlayer.getLayoutParams().height = (int) height;
            mSuperVideoPlayer2.getLayoutParams().height = (int) height;
            mSuperVideoPlayer.getLayoutParams().width = (int) width;
            mSuperVideoPlayer2.getLayoutParams().width = (int) width;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.video_player_item_1);
        mSuperVideoPlayer2 = (SuperVideoPlayer) findViewById(R.id.video_player_item_2);
        layout_video = (LinearLayout) findViewById(R.id.layout_video);
        layout_video.setVisibility(View.GONE);
        mPlayBtnView = findViewById(R.id.play_btn);
        file_choose = (ImageView) findViewById(R.id.iv_choose_mv);
        file_choose.setOnClickListener(this);
        mPlayBtnView.setOnClickListener(this);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);
        mSuperVideoPlayer2.setVideoPlayCallback(mVideoPlayCallback);
        startDLNAService();
    }

    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            mSuperVideoPlayer2.setPageType(MediaController.PageType.SHRINK);
        }
    }

    private void startDLNAService() {
        // Clear the device container.
        DLNAContainer.getInstance().clear();
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        startService(intent);
    }

    private void stopDLNAService() {
        Intent intent = new Intent(getApplicationContext(), DLNAService.class);
        stopService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_FILE_EXPLORER:
                    filePath= data.getStringExtra("key");
                    file_choose.setVisibility(View.GONE);
                    layout_video.setVisibility(View.VISIBLE);
                    mPlayBtnView.performClick();
                    break;
            }

        }
    }
}
