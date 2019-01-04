package com.example.wisetest;


import com.wise.mediarec.Recorder.NativeCamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;




@SuppressLint({ "NewApi", "ShowToast" })
public class NativeCameraActivity extends Activity 
implements OnClickListener,SurfaceHolder.Callback
{
	private static final String TAG = "NativeCameraActivity";
	
    private ImageButton mBtnStartStop;
    private Chronometer mTimer;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
	//private Camera mCamera;
	//private Camera.Parameters mParameters;
	
	NativeCamera mCamera = new NativeCamera();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFeature();
        setContentView(R.layout.activity_rec_native);
        initView();
    }
    
    private void initWindowFeature() 
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 选择支持半透明模式,在有SurfaceView的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }
    
    private void initView() {
    	mTimer			= (Chronometer) findViewById(R.id.crm_count_time);
    	
        mSurfaceView  	= (SurfaceView)findViewById(R.id.capture_surfaceview);
        mBtnStartStop 	= (ImageButton) findViewById(R.id.ib_record);
        mBtnStartStop.setOnClickListener(this);
        
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
//    	mCamera		= Camera.open();
//        mParameters	= mCamera.getParameters();
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//连续对焦 
//        mParameters.setPreviewSize(320, 240);
//        mCamera.setParameters(mParameters);
//        try {
//            mCamera.setPreviewDisplay(holder);
//            mCamera.startPreview();
//        } catch (IOException e) {
//            Log.d(TAG,"设置相机预览失败",e);
//            e.printStackTrace();
//        }
		
		mCamera.OpenCamera(0, "com.example.wisetest");
		mCamera.StartPreview(holder.getSurface());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
//        mHolder.removeCallback(this);
//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//        mCamera.release();
//        mCamera = null;
		mCamera.StopPreview();
		mCamera.CloseCamera();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}