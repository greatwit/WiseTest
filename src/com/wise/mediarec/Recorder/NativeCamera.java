package com.wise.mediarec.Recorder;

import android.util.Log;
import android.view.Surface;

@SuppressWarnings("deprecation")
public class NativeCamera
{
	private static String TAG = NativeCamera.class.getSimpleName();
    static {
		try {
			System.loadLibrary("NativeCamera");
		}
		catch(Throwable e) { 
			Log.e(TAG, "load library failed error:"+e.toString());
		}
    }
    
    public native boolean OpenCamera(int camid, String packName);
    public native boolean CloseCamera();
    public native String  GetCameraParameter();
    public native void 	  SetCameraParameter(String param);
    public native void    StartPreview(Surface surface);
    public native void    StopPreview();
}

