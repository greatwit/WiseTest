package com.example.wisetest;

import com.wise.mediarec.Recorder.WisePlayer;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


 

public class UpperMediaPlayerActivity extends Activity implements View.OnClickListener {
    private ImageButton btnplay, btnstop, btnpause;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer; 
    private int position;
    private String url0 = "/sdcard/RecorderTest/upper.mp4";
    private String url1 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4"; //新闻 640x360
    private String url2 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov"; //big buck bunny  240X160
    private String url3 = "http://file.xjkb.com/group1/M00/00/1A/eEzdEFh8Z-mAQdc6AcR3DvBgTPU994.mp4";//小熊嘟嘟 1280X720
    private String url4 = "http://221.228.226.23/11/t/j/v/b/tjvbwspwhqdmgouolposcsfafpedmb/sh.yinyuetai.com/691201536EE4912BF7E4F1E2C67B8119.mp4";//喜欢你1080P
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);
        btnplay = (ImageButton) this.findViewById(R.id.btnplay);
        btnstop = (ImageButton) this.findViewById(R.id.btnstop);
        btnpause = (ImageButton) this.findViewById(R.id.btnpause);

        btnstop.setOnClickListener(this);
        btnplay.setOnClickListener(this);
        btnpause.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);

        // 设置SurfaceView自己不管理的缓冲区
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (position > 0) {
                    try {
                        // 开始播放
                        play();
                        // 并直接从指定位置开始播放
                        mediaPlayer.seekTo(position);
                        position = 0;
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnplay:
                play();
                break;

            case R.id.btnpause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                break;

            case R.id.btnstop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        // 先判断是否正在播放
        if (mediaPlayer.isPlaying()) {
            // 如果正在播放我们就先保存这个播放位置
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
    
    private void play() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            Uri uri = Uri.parse(url0);
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            // 把视频画面输出到SurfaceView
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.prepare();
            // 播放
            mediaPlayer.start();
            Toast.makeText(this, "开始播放！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
    }
}