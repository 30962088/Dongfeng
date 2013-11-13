package com.media.dongfeng;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.media.dongfeng.model.User;
import com.media.dongfeng.utils.Utils;
import com.media.dongfeng.view.BottomTabView;

public class MainTabActivity extends TabActivity {

    public static final String SUCAI_TAG = "sucai";
    public static final String HUODONG_TAG = "huodong";
    public static final String SETTING_TAG = "shezhi";
    
    private BottomTabView mBottomTabView;
    private TabHost mHost;
    
    private View mBtnInfo;
    private View mBtnSetting;
    
    private Intent mSucaiIntent;
    
    private Intent mInfoIntent;
    
    private int defaultFlags;
    
    public static User mUser;
    
    public static int mScreenWidth;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
       
        
        PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY, 
				com.media.dongfeng.push.Utils.getMetaValue(this, "api_key"));
        initView();
        setDisplay();
        if (Utils.loadUser(this) == null) {
            selectTab(SETTING_TAG);
        } else {
            selectTab(SUCAI_TAG);
            if(mUser != null){
            	PushManager.setTags(this, new ArrayList<String>(){{
            		add(mUser.mid);
            	}});
            }
        }
        
        
    }
    
    private void setDisplay () {
        DisplayMetrics  dm = new DisplayMetrics();  
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        int screenWidth = dm.widthPixels;
        mScreenWidth = screenWidth;
    }

    private void initView() {
        setContentView(R.layout.frame_maintabs);
        mBottomTabView = (BottomTabView) findViewById(R.id.bottomTabView);
        mBtnInfo = findViewById(R.id.btn_huodong_ly);
        mBtnSetting = findViewById(R.id.btn_shezhi_ly);
        mBottomTabView.setBottomTabChangeListener(new BottomTabView.OnBottomTabChangeListener() {
            @Override
            public boolean onSelected( int viewId ) {
                mUser = Utils.loadUser(MainTabActivity.this);
                // TODO Auto-generated method stub
                if (viewId == R.id.btn_sucai_ly) {
                    if (mUser == null) {
                        return false;
                    }
                    mHost.setCurrentTabByTag(SUCAI_TAG);
                    mSucaiIntent.setFlags(defaultFlags);
                    return true;
                } else if (viewId == R.id.btn_huodong_ly) {
                    if (mUser == null) {
                        return false;
                    }
                    mHost.setCurrentTabByTag(HUODONG_TAG);
                    mInfoIntent.setFlags(defaultFlags);
                    return true;
                } else if (viewId == R.id.btn_shezhi_ly) {
                    mHost.setCurrentTabByTag(SETTING_TAG);
                    return true;
                }
                
                return false;
            }
        });
        mHost = getTabHost();
        initTabIntent();
    }
    
    private void initTabIntent() {
        mSucaiIntent = new Intent(this, SucaiActivity.class);
        mInfoIntent = new Intent(this, InfoActivity.class);
        defaultFlags = mInfoIntent.getFlags();
        Intent settingIntent = new Intent(this, SettingActivity.class);
        
        mHost.addTab(mHost
                .newTabSpec(SUCAI_TAG)
                .setIndicator(SUCAI_TAG, null)
                .setContent(mSucaiIntent));
        mHost.addTab(mHost
                .newTabSpec(HUODONG_TAG)
                .setIndicator(HUODONG_TAG, null)
                .setContent(mInfoIntent));
        mHost.addTab(mHost
                .newTabSpec(SETTING_TAG)
                .setIndicator(SETTING_TAG, null)
                .setContent(settingIntent));
        
        
    }
    
    public void selectTab(String tab) {
        mBottomTabView.performClickItem(tab);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        
    }
    
    public Intent getmSucaiIntent() {
		return mSucaiIntent;
	}
    
    public Intent getmInfoIntent() {
		return mInfoIntent;
	}
    
    @Override
    protected void onNewIntent(Intent intent) {
    	// TODO Auto-generated method stub
    	
    	super.onNewIntent(intent);
    	
    	if(intent != null && mInfoIntent != null){
    		String s = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
            if(s != null){
            	mInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
    	}
    	
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

}
