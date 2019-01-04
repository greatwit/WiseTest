package com.wise.mediarec.Recorder;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

@SuppressWarnings("deprecation")
public class CameraSrc
{
	final String KEY_MIME 	= "mime";
    final String KEY_WIDTH 	= "width";
    final String KEY_HEIGHT = "height";
    
	private static String TAG = CameraSrc.class.getSimpleName();
    static {
		try {
			System.loadLibrary("CodecBase6");
			System.loadLibrary("Camera6");
			System.loadLibrary("CameraSrc");
		}
		catch(Throwable e) { 
			Log.e(TAG, "load library failed error:"+e.toString());
		}
    }
    
	@SuppressLint("NewApi")
	public boolean StartCameraSrc(int width, int height, Surface surface, String packName)
	{
		Map<String, Object> mMap = new HashMap();
		mMap.put(KEY_MIME, "video/avc");
		mMap.put(KEY_WIDTH, new Integer(width));
		mMap.put(KEY_HEIGHT, new Integer(height));
		mMap.put(MediaFormat.KEY_COLOR_FORMAT, new Integer(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar)); 
		mMap.put(MediaFormat.KEY_BIT_RATE, 2500000);
		mMap.put(MediaFormat.KEY_FRAME_RATE, 25);
		mMap.put(MediaFormat.KEY_I_FRAME_INTERVAL, 2); //i frame
		
		
        String[] keys 	= null;
        Object[] values = null;


        keys = new String[mMap.size()];
        values = new Object[mMap.size()];

        int i = 0;
        for (Map.Entry<String, Object> entry: mMap.entrySet()) 
        {
            keys[i] = entry.getKey();
            values[i] = entry.getValue();
            ++i;
        }
        
        StartCameraEncodec(keys, values, null, MediaCodec.CONFIGURE_FLAG_ENCODE, packName);
        
        String param = GetCameraParameter();
        GreatCamera p  = new GreatCamera();
        GreatCamera.Parameters gp = p.getParameters(param);
        gp.setPreviewFormat(ImageFormat.NV21);
        gp.setPreviewSize(width, height);
        String flatParam = gp.flatten();
        SetCameraParameter(flatParam);
        SetDisplayOrientation(90);
//        
        StartCameraVideo(surface);
//        
        Log.e("SendEncodeActivity", " camera param:"+flatParam);
        
        //mCamera.setParameters(parameters);
        return true;
	}   
	
	public native boolean StartCameraEncodec(String[] keys, Object[] values, 
			Surface surface, int flags, String packName);
	public native boolean StartCameraVideo(Surface surface);
	public native boolean StopCameraEncodec();
	public native String  GetCameraParameter();
	public native boolean SetCameraParameter(String param);
	public native boolean SetDisplayOrientation(int value);
	
}

