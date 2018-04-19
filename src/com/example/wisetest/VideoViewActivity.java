package com.example.wisetest;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoViewActivity extends Activity {
    private Button bt_mediaPlayer, bt_videoview;
    private EditText url;
    private VideoView videoview;
    private MediaController mMediaController;
    private String url0 = "/sdcard/RecorderTest/upper.mp4";
    private String url1 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url2 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url3 = "http://42.96.249.166/live/388.m3u8";
    private String url4 = "http://61.129.89.191/ThroughTrain/download.html?id=4035&flag=-org-"; //音频url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        bt_mediaPlayer = (Button) findViewById(R.id.bt_mediaPlayer);
        url = (EditText) findViewById(R.id.url);
        bt_videoview = (Button) findViewById(R.id.bt_videoview);
        videoview = (VideoView) findViewById(R.id.video);

        mMediaController = new MediaController(this);
        videoview.setMediaController(mMediaController);

        url.setText(url0);
        bt_videoview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loadView(url.getText().toString());
            }
        });

        bt_mediaPlayer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoViewActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });
    }


    public void loadView(String path) {
        Uri uri = Uri.parse(path);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
     //         mp.setLooping(true);
                mp.start();// 播放
                Toast.makeText(VideoViewActivity.this, "开始播放！", Toast.LENGTH_LONG).show();
            }
        });

        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(VideoViewActivity.this, "播放完毕", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

