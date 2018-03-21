package com.example.wisetest.fragment;

import com.example.wisetest.R;
import com.example.wisetest.recorder.RecorderMainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 视频播放的Fragment
 * 
 * @author yanfa06
 * 
 */
public class ServiceFragment extends Fragment
		implements OnClickListener 
{
	private Context mContext;
	private final String TAG = "ServiceFragment";
	private View view;
	private RelativeLayout item_qr_rl;
	
	private final static int SCANNIN_GREQUEST_CODE = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = getActivity();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = LayoutInflater.from(mContext).inflate(R.layout.fragment_service,
				null);
		init();
		return view;
	}

	private void init() {
		item_qr_rl = (RelativeLayout) view.findViewById(R.id.item_qr_rl);
		item_qr_rl.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w(TAG, "onActivityResult:" + requestCode + " " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			//if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String content = bundle.getString("result");
				Log.w(TAG, "onActivityResult content:" + content);
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_qr_rl:
			startActivity(new Intent().setClass(mContext, RecorderMainActivity.class));
		break;
		default:
			break;
		}
	}

}
