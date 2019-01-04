
package com.example.wisetest;

import com.wise.mediarec.Recorder.CameraSrc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;


@SuppressWarnings("deprecation")
@SuppressLint("UseValueOf")
public class CameraSrcActivity extends Activity implements SurfaceHolder.Callback ,PreviewCallback, OnClickListener
{
	private String TAG = CameraSrcActivity.class.getSimpleName();
	
	private final int width 	= 1280;//1920;//
	private final int height 	= 720; //1080;//
	
	private SurfaceHolder mHolder 		= null;
	private Button btEncodecStart		= null;
	
    
    private CameraSrc mCameraSrc  	=  new CameraSrc();
    
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_native_encode);
		SurfaceView sfv_video = (SurfaceView) findViewById(R.id.sfv_video);
		mHolder = sfv_video.getHolder();
		mHolder.addCallback(this);
		
		btEncodecStart	 = (Button)findViewById(R.id.btEncodecStart);
		btEncodecStart.setOnClickListener(this);
	}

	
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub

		mCameraSrc.StartCameraSrc(width, height, holder.getSurface(), "com.example.wisetest");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub
		
		mCameraSrc.StopCameraEncodec();
	}

	public void onPreviewFrame(byte[] data, Camera camera) 
	{
		// TODO Auto-generated method stub
		Log.e(TAG, "camera data:" + data.length);
		
		int uvlen = width*height/2;
		int ylen  = width*height;
		for(int i=0;i<uvlen;)
		{
			byte tmp = data[ylen+i];
			data[ylen+i] = data[ylen+i+1];
			data[ylen+i+1] = tmp;
			i+=2;
		}
		
		//mCameraSrc.CodecSenderData(data, data.length);
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		    case R.id.btEncodecStart:
				break;
		}
	}
	
}


