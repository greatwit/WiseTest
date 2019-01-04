package com.wise.mediarec.Recorder;

import android.hardware.Camera;
import android.util.Log;

@SuppressWarnings("deprecation")
public class CamCodec
{
	private static String TAG = CamCodec.class.getSimpleName();
    static {
		try {
			System.loadLibrary("CamCodec");
		}
		catch(Throwable e) { 
			Log.e(TAG, "load library failed error:"+e.toString());
		}
    }
    
    public native int StartCamCodec(Camera c);
    public native int StopCamCodec();
}

