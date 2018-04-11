
package com.example.wisetest;

import org.webrtc.videoengine.VideoCaptureShow;
import org.webrtc.webrtcdemo.VideoEngine;

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

public class RtcCameraActivity extends Activity// implements MediaEngineObserver
{
  private String  TAG = getClass().getSimpleName();

  private ImageButton btStartStopCall;
  private TextView tvStats;

  // Remote and local stream displays.
  private LinearLayout llLocalSurface;
  
  private VideoCaptureShow mVideoCapture;
  private String mDestip;
  
  //static public MediaEngine mediaEngine = null;
  static public VideoEngine mVideoEngine;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Global settings.
    requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    
    mDestip = SysConfig.getSaveAddr(this);
    
	Log.w(TAG, "get destip:" + mDestip );
	
    setContentView(R.layout.activity_webrtc);
    llLocalSurface  = (LinearLayout) findViewById(R.id.llRemoteView);

/*
    // Load all settings dictated in xml.
    mediaEngine = new MediaEngine(this);
    mediaEngine.setRemoteIp(destip);//127.0.0.1 192.168.250.208
    mediaEngine.setTrace(true);

    mediaEngine.setSendVideo(true);
    mediaEngine.setVideoCodec(0);
    // TODO(hellner): resolutions should probably be in the xml as well.
    mediaEngine.setResolutionIndex(MediaEngine.numberOfResolutions() - 3);
    mediaEngine.setVideoTxPort(11111);
    mediaEngine.setNack(true);
    */
    
    mVideoEngine = new VideoEngine(this);
    mVideoEngine.initEngine();
    
    tvStats = (TextView) findViewById(R.id.tvStats);
    
    Button btSwitchCamera = (Button) findViewById(R.id.btSwitchCamera);
//    if (getEngine().hasMultipleCameras()) {
//      btSwitchCamera.setOnClickListener(new View.OnClickListener() {
//        public void onClick(View button) {
//          toggleCamera((Button) button);
//        }
//        });
//    } else {
      btSwitchCamera.setEnabled(false);
    //}
    //btSwitchCamera.setText(getEngine().frontCameraIsSet() ? R.string.backCamera : R.string.frontCamera);

    btStartStopCall = (ImageButton) findViewById(R.id.btStartStopCall);
    //btStartStopCall.setBackgroundResource(getEngine().isRunning() ? R.drawable.record_stop : R.drawable.record_start);
    btStartStopCall.setOnClickListener(new View.OnClickListener() {
        public void onClick(View button) {
        	toggleStart();
        }
      });
    
    //getEngine().setResolutionIndex(SysConfig.getSaveResolution(this));
    Log.w(TAG, "spCodecSize.setSelection:"+ SysConfig.getSaveResolution(this) );

    //getEngine().setObserver(this);
    
    mVideoCapture = new VideoCaptureShow(this);
  }
  
  //private MediaEngine getEngine() { return mediaEngine; }

  private void setViews() {
    SurfaceView svLocal = mVideoCapture.getLocalSurfaceView();
    //svLocal.setZOrderOnTop(true);
    if (svLocal != null) {
      llLocalSurface.addView(svLocal);
    }
  }

  private void clearViews() {
    SurfaceView svLocal = mVideoCapture.getLocalSurfaceView();
    if (svLocal != null) {
      llLocalSurface.removeView(svLocal);
    }
  }

  // tvStats need to be updated on the UI thread.
  public void newStats(final String stats) {
    	runOnUiThread(new Runnable() {
        public void run() {
          tvStats.setText(stats);
        }
      });
  }

  public void toggleStart() {
  	if(mVideoEngine.isSendRunning())
  	{
         mVideoEngine.stopSend();
         stopAll();
  	}else
  	{
         mVideoEngine.startSend(mDestip, 11111, true, 3, 1);
         startCall();
  	}

    btStartStopCall.setBackgroundResource(mVideoEngine.isSendRunning() ? R.drawable.record_stop : R.drawable.record_start);
  }
  
  private void toggleCamera(Button btSwitchCamera) {
    SurfaceView svLocal = mVideoCapture.getLocalSurfaceView();
    boolean resetLocalView = svLocal != null;
    if (resetLocalView) {
      llLocalSurface.removeView(svLocal);
    }
    //getEngine().toggleCamera();
    if (resetLocalView) {
      svLocal = mVideoCapture.getLocalSurfaceView();
      llLocalSurface.addView(svLocal);
    }
//    btSwitchCamera.setText(getEngine().frontCameraIsSet() ?
//        R.string.backCamera :
//        R.string.frontCamera);
  }

  public void stopAll() {
	mVideoCapture.stopCapture();
    clearViews();
    //getEngine().stopVideoSend();
  }

  private void startCall() {
    //getEngine().startVideoSend();
	int position = SysConfig.getSaveResolution(this);
	int width = VideoEngine.RESOLUTIONS[position][0];
	int height = VideoEngine.RESOLUTIONS[position][1];
    mVideoCapture.startCapture(width, height, 2000, 35000);
    Log.w(TAG,"startCall "+"width:"+width+" height:"+height);
    setViews();
  }
  
  @Override
  public void onDestroy()
  {
	    if (mVideoEngine.isSendRunning()){
	    	mVideoEngine.stopSend();
	        stopAll();
	    }
	    mVideoEngine.deInitEngine();
	    super.onDestroy();
  }


  
}

