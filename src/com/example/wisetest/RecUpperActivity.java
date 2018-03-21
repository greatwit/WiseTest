package com.example.wisetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.AsyncTask;
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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;


@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class RecUpperActivity extends Activity implements 
			OnClickListener, SurfaceHolder.Callback, Runnable
{
    private static final String TAG = "MainActivity";
    
    private ImageButton mBtnStartStop;
    //private ImageButton mBtnSet;
    //private ImageButton mBtnShowFile;
    private Chronometer mTimer;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
	private Camera mCamera;
	private Camera.Parameters mParameters;
    private MediaRecorder mMediaRecorder = null;
    
	LocalServerSocket server;
	LocalSocket receiver, sender;
	private boolean mMediaRecorderRecording = false;
	private final int MAXFRAMEBUFFER = 204800;//200K
	private byte[] h264frame = new byte[MAXFRAMEBUFFER];
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFeature();
        setContentView(R.layout.activity_record);
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
        //mBtnSet.setOnClickListener(this);
        //mBtnShowFile.setOnClickListener(this);
        
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
    }
    
	//初始化LocalServerSocket LocalSocket
	private void InitLocalSocket()
	{
		try {
			server = new LocalServerSocket("Wise");
			receiver = new LocalSocket();
			
			receiver.connect(server.getLocalSocketAddress());
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(50000);
			
			sender = server.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(50000);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			this.finish();
			return;
		}
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
            	mMediaRecorderRecording = false;
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

    public boolean startRecording() {
        if (prepareMediaRecorder()) {

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
            return true;
        } else {
            releaseMediaRecorder();
        }
        return false;
    }

    public void stopRecording() 
    {
        if (mMediaRecorder != null) {
//    		try {
//    			server.close();
//    			receiver.close();
//    			sender.close();
//    		} catch (IOException e) {
//    			Log.e(TAG, e.toString());
//    		}
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
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));//QUALITY_480P QUALITY_720P QUALITY_1080P
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setVideoFrameRate(15);
        
        String path = getSDPath();
        if (path != null) 
        {
            File dir = new File(path + "/RecorderTest");
            if (!dir.exists()) {
                dir.mkdir();
            }
            path = path + "/RecorderTest/" + "upper.mp4";//+ getDate()
        }
        else
        	path = "/sdcard/"+getDate() + ".mp4";
        
        mMediaRecorder.setOutputFile(path);//sender.getFileDescriptor()
        mMediaRecorderRecording = true;
        
        //new Thread(this).start();
        
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
		try 
		{
			DataInputStream dataInput = new DataInputStream(receiver.getInputStream());
			//先读取ftpy box and mdat box, 目的是skip ftpy and mdat data,(decisbe by phone)
			//dataInput.read(h264frame, 0, StartMdatPlace);
			//Log.w(TAG,"StartMdatPlace :" + StartMdatPlace);

			int h264length =0;
			
			while(mMediaRecorderRecording) 
			{
				if(dataInput.readBoolean())
				{
					//h264length = dataInput.readInt();
					//Log.w(TAG, "h264 length1 :" + h264length);
					h264length = dataInput.read(h264frame);
					Log.w(TAG, "h264 length:" + h264length);
				}
				Thread.sleep(50);
			}
			dataInput.close();
		} catch (Exception e) {
			e.printStackTrace();
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

        return date;
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
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mParameters.setPreviewSize(320, 240);
        //mParameters.setPictureSize(width, height);
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
                    Log.d(TAG,"预览帧大小："+String.valueOf(data.length));
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

    private class ProcessFrameAsyncTask extends AsyncTask<byte[],Void,String> {
        @Override
        protected String doInBackground(byte[]... params) {
            processFrame(params[0]);
            return null;
        }

        private void processFrame(byte[] frameData) {
            //Log.i(TAG, "正在处理预览帧...");
            //Log.i(TAG, "预览帧大小"+String.valueOf(frameData.length));
        }
    }
}

