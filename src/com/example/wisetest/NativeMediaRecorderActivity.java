package com.example.wisetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.wise.mediarec.Recorder.WiseRecorder;
import com.wise.mediarec.Recorder.WiseRecorder6;



@SuppressLint({ "NewApi", "ShowToast" })
@SuppressWarnings("deprecation")
public class NativeMediaRecorderActivity extends Activity implements 
			OnClickListener, SurfaceHolder.Callback, Runnable
{
    private static final String TAG = "MainActivity";
    
    private ImageButton mBtnStartStop;
    private Chronometer mTimer;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
	private Camera mCamera;
	private Camera.Parameters mParameters;
    private WiseRecorder6 mMediaRecorder = null;

	
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
    
    private void initView() 
    {
    	mTimer			= (Chronometer) findViewById(R.id.crm_count_time);
    	
        mSurfaceView  	= (SurfaceView)findViewById(R.id.capture_surfaceview);
        mBtnStartStop 	= (ImageButton) findViewById(R.id.ib_record);
        mBtnStartStop.setOnClickListener(this);
        
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
    }
    
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.ib_record:
            Log.d(TAG,"录像");
            
            if (mMediaRecorder!=null) 
            {
                stopRecording();
                mTimer.stop();
                mBtnStartStop.setBackgroundResource(R.drawable.record_start);
                Log.w(TAG,"停止录像");
            } else {
                if (startRecording()) 
                {
                    mTimer.setBase(SystemClock.elapsedRealtime());
                    mTimer.start();
                    mBtnStartStop.setBackgroundResource(R.drawable.record_stop);
                    Log.w(TAG,"开始录像");
                }
            }
			break;
		}
	}

    public boolean startRecording() 
    {
        if (prepareMediaRecorder()) 
        {
            new Thread(this).start();
            return true;
        } else {
            releaseMediaRecorder();
        }
        return false;
    }

    public void stopRecording() 
    {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
        releaseMediaRecorder();
    }
    
	private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    
    @SuppressLint("NewApi")
	private boolean prepareMediaRecorder() 
    {
    	//InitLocalSocket();
        mMediaRecorder = new WiseRecorder6(); 
        mCamera.unlock();
        mMediaRecorder.reset();
        mMediaRecorder.setCamera(mCamera);
        
        //mMediaRecorder.setAudioSource(WiseRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(WiseRecorder.VideoSource.CAMERA);
        
        mMediaRecorder.setOutputFormat(WiseRecorder.OutputFormat.MPEG_4);
//        // 设置录制的视频编码和音频编码
//        mMediaRecorder.setVideoEncoder(WiseRecorder6.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(WiseRecorder6.AudioEncoder.AAC);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));//QUALITY_480P QUALITY_720P QUALITY_1080P
        //mMediaRecorder.setVideoSize(1280, 720);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setVideoFrameRate(15);
        
        String path = getSDPath();
        if (path != null) 
        {
            File dir = new File(path + "/RecorderTest");
            if (!dir.exists()) {
                dir.mkdir();
            }
            path = path + "/RecorderTest/" + "natrec.mp4";
        }
        else
        	path = "/sdcard/"+getDate() + ".mp4";
        Toast.makeText(this, path, Toast.LENGTH_LONG).show();
        mMediaRecorder.setOutputFile(path);//sender.getFileDescriptor()  
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            releaseMediaRecorder();
            e.printStackTrace();
        }
        return true;
    }

	public void run() 
	{
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
        	Log.e(TAG, "MediaRecorder error:" + e);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "MediaRecorder error:" + e);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "MediaRecorder error:" + e);
        }
	}
	
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取系统时间
     */
    public static String getDate(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1 )+ day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return "mytest";//date;
    }
    
    /**
     * 获取SD path
     */
    public String getSDPath(){
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();// 获取外部存储的根目录
            return sdDir.toString();
        }
        return null;
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	mCamera		= Camera.open();
        mParameters	= mCamera.getParameters();
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);//连续对焦 
        mParameters.setPreviewSize(320, 240);
        mCamera.setParameters(mParameters);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    Log.w(TAG,"自动对焦成功");
                }
            }
        });
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

            //下面这个方法能帮我们获取到相机预览帧，我们可以在这里实时地处理每一帧
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    //Log.i(TAG, "获取预览帧...");
                    //new ProcessFrameAsyncTask().execute(data);
                    //Log.i(TAG,"预览帧大小："+String.valueOf(data.length));//320*240 YUV:115200
                }
            });
        } catch (IOException e) {
            Log.d(TAG,"设置相机预览失败",e);
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

}

