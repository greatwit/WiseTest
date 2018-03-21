package com.example.wisetest;

import java.io.IOException;
import java.nio.ByteBuffer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaMuxer.OutputFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class ExtractorActivity extends Activity {
    
    private static final String SDCARD_PATH  = Environment.getExternalStorageDirectory().getPath();

    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer; 
    
    private final String TAG = "ExtractorActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extractor);
        new Thread(new Runnable() {            
            @Override
            public void run() {
                try {
                    process();
                } 
                catch (IOException e) {                
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    @SuppressLint("NewApi") 
    protected boolean process() throws IOException {

        mMediaExtractor = new MediaExtractor();          
        mMediaExtractor.setDataSource(SDCARD_PATH+"/input.mp4");                
                
        int mVideoTrackIndex = -1;
        int framerate = 0;
        for(int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            Log.w(TAG, "mime:" + mime);
            if(!mime.startsWith("video/")) {                
                continue;
            }
            framerate = format.getInteger(MediaFormat.KEY_FRAME_RATE);            
            mMediaExtractor.selectTrack(i);
            mMediaMuxer = new MediaMuxer(SDCARD_PATH+"/ouput.mp4", OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVideoTrackIndex = mMediaMuxer.addTrack(format);  
            mMediaMuxer.start();
            Log.w(TAG, "framerate:" + framerate);
        }
        
        if(mMediaMuxer == null) {
            return false;
        }
        
        BufferInfo info = new BufferInfo();
        info.presentationTimeUs = 0;
        ByteBuffer buffer = ByteBuffer.allocate(500*1024);        
        while(true) {
            int sampleSize = mMediaExtractor.readSampleData(buffer, 0);
            Log.w(TAG, "sampleSize:" + sampleSize);
            if(sampleSize < 0) {
                break;
            }
            mMediaExtractor.advance();
            info.offset = 0;
            info.size = sampleSize;
            info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;        
            info.presentationTimeUs += 1000*1000/framerate;
            mMediaMuxer.writeSampleData(mVideoTrackIndex,buffer,info);
        }

        mMediaExtractor.release();
        
        mMediaMuxer.stop();
        mMediaMuxer.release();
        
        return true;
    }
}


