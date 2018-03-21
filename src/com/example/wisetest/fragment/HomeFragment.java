package com.example.wisetest.fragment;


import com.example.wisetest.CameraTestActivity;
import com.example.wisetest.ExtractorActivity;
import com.example.wisetest.MediaPlayerActivity;
import com.example.wisetest.R;
import com.example.wisetest.RecNativeActivity;
import com.example.wisetest.RecUpperActivity;
import com.example.wisetest.VideoViewActivity;
import com.example.wisetest.WebrtcActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 主页的Fragment
 * 
 * @author yanfa06
 * 
 */
public class HomeFragment extends Fragment
		implements OnClickListener{
	private final String TAG = "HomeFragment";
	private Context mContext;
	private View view;
	private Button btnEncode = null, btnDecode = null, btnUpperRec = null, 
			btnUpperPlay = null, btnNativeRec = null, btnExtractor = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = getActivity();
		// 注册EventBus
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = LayoutInflater.from(mContext).inflate(R.layout.fragment_home, null);
		btnEncode = (Button)view.findViewById(R.id.btnEncode);
		btnEncode.setOnClickListener(this);
		
		btnDecode = (Button)view.findViewById(R.id.btnDecode);
		btnDecode.setOnClickListener(this);
		
		btnUpperRec = (Button)view.findViewById(R.id.btnUpperRec);
		btnUpperRec.setOnClickListener(this);
		
		btnUpperPlay = (Button)view.findViewById(R.id.btnUpperPlay);
		btnUpperPlay.setOnClickListener(this);
		
		btnNativeRec = (Button)view.findViewById(R.id.btnNativeRec);
		btnNativeRec.setOnClickListener(this);
		
		btnExtractor = (Button)view.findViewById(R.id.btnExtractor);
		btnExtractor.setOnClickListener(this);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch(v.getId())
		{
			case R.id.btnEncode:
				intent.setClass(mContext, WebrtcActivity.class);
				startActivity(intent);
				break;
			case R.id.btnDecode:
				intent.setClass(mContext, CameraTestActivity.class);
				startActivity(intent);
				break;
			case R.id.btnUpperRec:
				intent.setClass(mContext, RecUpperActivity.class);
				startActivity(intent);
				break;
			case R.id.btnUpperPlay:
				intent.setClass(mContext, MediaPlayerActivity.class);
				startActivity(intent);
				break;
			case R.id.btnNativeRec:
				intent.setClass(mContext, RecNativeActivity.class);
				startActivity(intent);
				break;
				
			case R.id.btnExtractor:
				intent.setClass(mContext, ExtractorActivity.class);
				startActivity(intent);
				break;
		}
	}


}
