/*
 *  Copyright (c) 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.example.wisetest;

import org.webrtc.videoengine.ViERenderer;
import org.webrtc.webrtcdemo.VideoEngine;
import org.webrtc.webrtcdemo.VoiceEngine;

import com.example.wisetest.recorder.util.SysConfig;
import com.example.wisetest.views.MediaEngineObserver;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraRecvActivity extends Activity implements MediaEngineObserver
{
  private String  TAG = getClass().getSimpleName();

  private ImageButton btStartStopCall;
  private TextView tvStats;

  // Remote and local stream displays.
  private LinearLayout llRemoteSurface;
  SurfaceView remoteSurfaceView;
  
  
  static private VideoEngine mVideoEngine;
  static private VoiceEngine mVoiceEngine;
  
  private String mDestip;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Global settings.
    requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	
    setContentView(R.layout.activity_webrtc);
    llRemoteSurface = (LinearLayout) findViewById(R.id.llRemoteView);

    tvStats = (TextView) findViewById(R.id.tvStats);
    setViews();
    
    mDestip = SysConfig.getSaveAddr(this);
    
    mVideoEngine = new VideoEngine(this);
    mVideoEngine.initEngine();
    
    mVoiceEngine = new VoiceEngine(this);
    mVoiceEngine.initEngine();
    mVoiceEngine.setSpeaker(true);

    btStartStopCall = (ImageButton) findViewById(R.id.btStartStopCall);
    btStartStopCall.setBackgroundResource(mVideoEngine.isRecvRunning() ? R.drawable.record_stop : R.drawable.record_start);
    btStartStopCall.setOnClickListener(new View.OnClickListener() {
        public void onClick(View button) {
          toggleStart();
        }
      });
    
    //Log.i(TAG, VideoCaptureDeviceInfoAndroid.getDeviceInfo());
  }
  

  private void setViews() {
    remoteSurfaceView = ViERenderer.CreateRenderer(this, true);
    if (remoteSurfaceView != null) {
      llRemoteSurface.addView(remoteSurfaceView);
      //llRemoteSurface.setRotation(90);
    }
  }

  private void removeViews() {
	    remoteSurfaceView = ViERenderer.CreateRenderer(this, true);
	    if (remoteSurfaceView != null) {
	      llRemoteSurface.removeView(remoteSurfaceView);
	    }
	  }
  
  // tvStats need to be updated on the UI thread.
  @Override
  public void newStats(final String stats) {
  	runOnUiThread(new Runnable() {
      public void run() {
        tvStats.setText(stats);
      }
    });
  }

  public void toggleStart() {
    if (mVideoEngine.isRecvRunning()) {
    	mVideoEngine.stopRecv();
    	
    	mVoiceEngine.stopVoice();
    } else {
    	mVideoEngine.startRecv(remoteSurfaceView, 11111, true, SysConfig.getSaveResolution(this));
        
    	int temp = SysConfig.getSavePlay(this);
      	 if((temp&0x4) != 0)
      		 mVoiceEngine.startVoice(mDestip, 11113, 11113);
    }
    btStartStopCall.setBackgroundResource(mVideoEngine.isRecvRunning() ? R.drawable.record_stop : R.drawable.record_start);
  }

  
  @Override
  public void onDestroy() {
	    if (mVideoEngine.isRecvRunning()){
	    	mVideoEngine.stopRecv();
	    }
	    removeViews();
	    remoteSurfaceView = null;
	    mVideoEngine.deInitEngine();
	    
	    if(mVoiceEngine.isVoiceRunning()) {
	    	mVoiceEngine.stopVoice();
	    }
	    mVoiceEngine.deInitEngine();
	    
	    super.onDestroy();
  }
  
}

