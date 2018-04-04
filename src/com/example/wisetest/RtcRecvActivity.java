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

import org.webrtc.videoengine.VideoCaptureDeviceInfoAndroid;
import org.webrtc.webrtcdemo.MediaEngine;
import org.webrtc.webrtcdemo.MediaEngineObserver;
import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

import com.example.wisetest.recorder.util.SysConfig;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RtcRecvActivity extends Activity  implements MediaEngineObserver
{

  private String  TAG = "WebrtcActivity";

  private ImageButton btStartStopCall;
  private TextView tvStats;

  // Remote and local stream displays.
  private LinearLayout llRemoteSurface;
  private LinearLayout llLocalSurface;
  
  private NativeWebRtcContextRegistry contextRegistry = null;
  static public MediaEngine mediaEngine = null;
  
   
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Global settings.
    requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    
    String destip = SysConfig.getSaveAddr(this);
    
	Log.w(TAG, "get destip:" + destip );
	
    setContentView(R.layout.activity_webrtc);
    llRemoteSurface = (LinearLayout) findViewById(R.id.llRemoteView);
    llLocalSurface  = (LinearLayout) findViewById(R.id.llLocalView);

    // Must be instantiated before MediaEngine.
    contextRegistry = new NativeWebRtcContextRegistry();
    contextRegistry.register(this);

    // Load all settings dictated in xml.
    mediaEngine = new MediaEngine(this);
    mediaEngine.setRemoteIp(destip);//127.0.0.1 192.168.250.208
    mediaEngine.setTrace(true);
    mediaEngine.setSendVideo(true);
    
    mediaEngine.setReceiveVideo(true);
    
    mediaEngine.setVideoCodec(0);
    // TODO(hellner): resolutions should probably be in the xml as well.
    mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() - 3);
    mediaEngine.setVideoTxPort(11111);
    mediaEngine.setVideoRxPort(11111);
    mediaEngine.setNack(true);
    
    
    int play = SysConfig.getSavePlay(this);
    if((play&0x4)==0x4)
    {
    	mediaEngine.setAudio(true);
        mediaEngine.setAudioCodec(mediaEngine.getIsacIndex());
        mediaEngine.setAudioRxPort(11113);
        mediaEngine.setAudioTxPort(11113);
        mediaEngine.setSpeaker(false);
        mediaEngine.setDebuging(false);
    }
    else
    	mediaEngine.setAudio(false);
    
    tvStats = (TextView) findViewById(R.id.tvStats);
    

    btStartStopCall = (ImageButton) findViewById(R.id.btStartStopCall);
    btStartStopCall.setBackgroundResource(getEngine().isRunning() ? R.drawable.record_stop : R.drawable.record_start);
    btStartStopCall.setOnClickListener(new View.OnClickListener() {
        public void onClick(View button) {
          toggleStart();
        }
      });
    
    getEngine().setResolutionIndex(SysConfig.getSaveResolution(this));
    Log.w(TAG, "spCodecSize.setSelection:"+ SysConfig.getSaveResolution(this) );

    getEngine().setObserver(this);
    
    Log.i(TAG, VideoCaptureDeviceInfoAndroid.getDeviceInfo());
  }
  
  private MediaEngine getEngine() { return mediaEngine; }

  private void setViews() {
    SurfaceView remoteSurfaceView = getEngine().getRemoteSurfaceView();
    if (remoteSurfaceView != null) {
      llRemoteSurface.addView(remoteSurfaceView);
    }
//    SurfaceView svLocal = getEngine().getLocalSurfaceView();
//    svLocal.setZOrderOnTop(true);
//    if (svLocal != null) {
//      llLocalSurface.addView(svLocal);
//    }
  }

  private void clearViews() {
    SurfaceView remoteSurfaceView = getEngine().getRemoteSurfaceView();
    if (remoteSurfaceView != null) {
      llRemoteSurface.removeView(remoteSurfaceView);
    }
//    SurfaceView svLocal = getEngine().getLocalSurfaceView();
//    if (svLocal != null) {
//      llLocalSurface.removeView(svLocal);
//    }
  }

  // tvStats need to be updated on the UI thread.
  public void newStats(final String stats) {
    	runOnUiThread(new Runnable() {
        public void run() {
          tvStats.setText(stats);
        }
      });
  }

  private void toggleCamera(Button btSwitchCamera) {
//    SurfaceView svLocal = getEngine().getLocalSurfaceView();
//    boolean resetLocalView = svLocal != null;
//    if (resetLocalView) {
//      llLocalSurface.removeView(svLocal);
//    }
//    getEngine().toggleCamera();
//    if (resetLocalView) {
//      svLocal = getEngine().getLocalSurfaceView();
//      llLocalSurface.addView(svLocal);
//    }
    btSwitchCamera.setText(getEngine().frontCameraIsSet() ?
        R.string.backCamera :
        R.string.frontCamera);
  }

  public void toggleStart() {
    if (getEngine().isRunning()) {
      stopAll();
    } else {
      startCall();
    }
    btStartStopCall.setBackgroundResource(getEngine().isRunning() ? R.drawable.record_stop : R.drawable.record_start);
  }

  public void stopAll() {
    clearViews();
    getEngine().stopVideoRecv();
  }

  private void startCall() {
    getEngine().startVideoRecv();
    setViews();
  }
  
  @Override
  public void onDestroy() 
  {
	    if (getEngine().isRunning())
	       stopAll();
	    mediaEngine.dispose();
	    contextRegistry.unRegister();
	    super.onDestroy();
  }
  
}

