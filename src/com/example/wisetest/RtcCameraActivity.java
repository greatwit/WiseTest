
package com.example.wisetest;

import org.webrtc.videoengine.VideoCaptureShow;
import org.webrtc.webrtcdemo.MediaEngine;
import org.webrtc.webrtcdemo.MediaEngineObserver;
import org.webrtc.webrtcdemo.NativeWebRtcContextRegistry;

import com.example.wisetest.recorder.util.SysConfig;


import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings({ "deprecation" })
public class RtcCameraActivity extends Activity implements MediaEngineObserver
{
  private String  TAG = getClass().getSimpleName();

  private ImageButton btStartStopCall;
  private TextView tvStats;

  // Remote and local stream displays.
  private LinearLayout llLocalSurface;
  
  private VideoCaptureShow mVideoCapture;
  
  private NativeWebRtcContextRegistry contextRegistry = null;
  static public MediaEngine mediaEngine = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Global settings.
    requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    
    String destip = SysConfig.getSaveAddr(this);
    
	Log.w(TAG, "get destip:" + destip );
	
    setContentView(R.layout.activity_webrtc);
    llLocalSurface  = (LinearLayout) findViewById(R.id.llRemoteView);

    // Must be instantiated before MediaEngine.
    contextRegistry = new NativeWebRtcContextRegistry();
    contextRegistry.register(this);

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
    
    tvStats = (TextView) findViewById(R.id.tvStats);
    
    Button btSwitchCamera = (Button) findViewById(R.id.btSwitchCamera);
    if (getEngine().hasMultipleCameras()) {
      btSwitchCamera.setOnClickListener(new View.OnClickListener() {
        public void onClick(View button) {
          toggleCamera((Button) button);
        }
        });
    } else {
      btSwitchCamera.setEnabled(false);
    }
    btSwitchCamera.setText(getEngine().frontCameraIsSet() ? R.string.backCamera : R.string.frontCamera);

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
    
    mVideoCapture = new VideoCaptureShow(this, 1, 0);
  }
  
  private MediaEngine getEngine() { return mediaEngine; }

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

  private void toggleCamera(Button btSwitchCamera) {
    SurfaceView svLocal = mVideoCapture.getLocalSurfaceView();
    boolean resetLocalView = svLocal != null;
    if (resetLocalView) {
      llLocalSurface.removeView(svLocal);
    }
    getEngine().toggleCamera();
    if (resetLocalView) {
      svLocal = mVideoCapture.getLocalSurfaceView();
      llLocalSurface.addView(svLocal);
    }
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
	mVideoCapture.stopCapture();
    clearViews();
    getEngine().stopVideoSend();
  }

  private void startCall() {
    getEngine().startVideoSend();
    mVideoCapture.startCapture(640, 480, 2000, 35000);
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
