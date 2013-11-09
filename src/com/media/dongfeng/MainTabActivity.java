package com.media.dongfeng;

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
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
                    return true;
                } else if (viewId == R.id.btn_huodong_ly) {
                    if (mUser == null) {
                        return false;
                    }
                    mHost.setCurrentTabByTag(HUODONG_TAG);
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
        Intent sucaiIntent = new Intent(this, SucaiActivity.class);
        Intent huodongIntent = new Intent(this, HuoDongActivity.class);
        Intent settingIntent = new Intent(this, SettingActivity.class);
        
        mHost.addTab(mHost
                .newTabSpec(SUCAI_TAG)
                .setIndicator(SUCAI_TAG, null)
                .setContent(sucaiIntent));
        mHost.addTab(mHost
                .newTabSpec(HUODONG_TAG)
                .setIndicator(HUODONG_TAG, null)
                .setContent(huodongIntent));
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
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

}
