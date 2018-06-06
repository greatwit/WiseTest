package com.example.wisetest;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import com.example.wisetest.fragment.CommonTabLayout;
import com.example.wisetest.fragment.CustomTabEntity;
import com.example.wisetest.fragment.HomeFragment;
import com.example.wisetest.fragment.OnTabSelectListener;
import com.example.wisetest.fragment.PersonalFragment;
import com.example.wisetest.fragment.ServiceFragment;
import com.example.wisetest.fragment.TabEntity;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

    private CommonTabLayout tabLayout;
    private TextView main_title_bar_text;
    private OnTabSelectListener mTabSelectListener;
    private int currentTabPosition = 0;// 当前Tab选中的位置
	private HomeFragment homeFragment;
	private ServiceFragment serviceFragment;
	private PersonalFragment personalFragment;
	
	private String[] mTitles = { "测试", "服务","信息" };//"服务", "购物"
	private int[] mIconUnselectIds = { R.drawable.ic_home_normal,
			R.drawable.ic_service_normal, 
			R.drawable.ic_personal_normal };//R.drawable.ic_service_normal, R.drawable.ic_shopping_normal,
	private int[] mIconSelectIds = { R.drawable.ic_home_select,
			R.drawable.ic_service_select,
			R.drawable.ic_personal_select };//R.drawable.ic_service_select, R.drawable.ic_shopping_select,

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initVIew();
        initFragment(savedInstanceState);
    }

    private void initVIew()
    {
		mTabSelectListener = new OnTabSelectListener() {
			@Override
			public void onTabSelect(int position) {
				SwitchTo(position);
			}
			@Override
			public void onTabReselect(int position) {

			}
		};
		tabLayout = (CommonTabLayout) findViewById(R.id.tab_layout);
		// 点击监听
		tabLayout.setOnTabSelectListener(mTabSelectListener);
		main_title_bar_text = (TextView)findViewById(R.id.main_title_bar_text);
    }
    
	/** 初始化碎片 */
	private void initFragment(Bundle savedInstanceState) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (savedInstanceState != null) {
			homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("homeFragment");
			serviceFragment = (ServiceFragment) getSupportFragmentManager().findFragmentByTag("serviceFragment");
			personalFragment = (PersonalFragment) getSupportFragmentManager().findFragmentByTag("personalFragment");
			currentTabPosition = savedInstanceState.getInt("HOME_CURRENT_TAB_POSITION");
		} else {
			homeFragment = new HomeFragment();
			serviceFragment = new ServiceFragment();
			personalFragment = new PersonalFragment();

			transaction.add(R.id.fl_body, homeFragment, "homeFragment");
			transaction.add(R.id.fl_body, serviceFragment, "serviceFragment");
			transaction.add(R.id.fl_body, personalFragment, "personalFragment");
		}
		transaction.commit();
		SwitchTo(currentTabPosition);
	}

	/** 切换 */
	private void SwitchTo(int position) {
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch (position) {
		// 主页
		case 0:
			transaction.show(homeFragment);
			transaction.hide(serviceFragment);
			// transaction.hide(shoppingFragment);
			transaction.hide(personalFragment);
			transaction.commitAllowingStateLoss();
			//ChangeTitleLayout(position);
			currentTabPosition = 0;
			main_title_bar_text.setText("WiseTest");
			// 重新获得已绑房产列表
			new Handler().post(new Runnable() {
				@Override
				public void run() {
				}
			});
			break;
		// 服务
		case 1:
			transaction.hide(homeFragment);
			transaction.show(serviceFragment);
			// transaction.hide(shoppingFragment);
			transaction.hide(personalFragment);
			transaction.commitAllowingStateLoss();
			//ChangeTitleLayout(position);
			currentTabPosition = 1;
			main_title_bar_text.setText("WiseTest");
			break;

		case 2:
			transaction.hide(homeFragment);
			transaction.hide(serviceFragment);
			// transaction.hide(shoppingFragment);
			transaction.show(personalFragment);
			transaction.commitAllowingStateLoss();
			//ChangeTitleLayout(position);
			currentTabPosition = 2;
			main_title_bar_text.setText("Local:"+getHostIP());
			break;
		default:
			break;
		}
	}
	
	/** 
	 * 获取ip地址 
	 * @return 
	 */  
	public static String getHostIP() {  
	  
	    String hostIp = null;  
	    try {  
	        Enumeration nis = NetworkInterface.getNetworkInterfaces();  
	        InetAddress ia = null;  
	        while (nis.hasMoreElements()) {  
	            NetworkInterface ni = (NetworkInterface) nis.nextElement();  
	            Enumeration<InetAddress> ias = ni.getInetAddresses();  
	            while (ias.hasMoreElements()) {  
	                ia = ias.nextElement();  
	                if (ia instanceof Inet6Address) {  
	                    continue;// skip ipv6  
	                }  
	                String ip = ia.getHostAddress();  
	                if (!"127.0.0.1".equals(ip)) {  
	                    hostIp = ia.getHostAddress();  
	                    break;  
	                }  
	            }  
	        }  
	    } catch (SocketException e) {  
	        Log.i("yao", "SocketException");  
	        e.printStackTrace();  
	    }  
	    return hostIp;  
	  
	}
	
	@Override
	protected void onResume() {
		// 如果debug模式被打开，显示监控
		// AbMonitorUtil.openMonitor(this);
		super.onResume();
		ArrayList<CustomTabEntity> data = new ArrayList<CustomTabEntity>();
		for (int i = 0; i < mTitles.length; i++) {
			data.add(new TabEntity(mTitles[i], mIconSelectIds[i],
					mIconUnselectIds[i]));
		}
		tabLayout.setTabData(data);
		tabLayout.setCurrentTab(currentTabPosition);
	}
	
}
