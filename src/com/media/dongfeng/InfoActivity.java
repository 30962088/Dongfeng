package com.media.dongfeng;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


public class InfoActivity extends BaseActivity {

    public static final String HUODONG_FRAGMENT = "huodong_fragment";
    public static final String HUODONG_DETAIL_FRAGMENT = "huodong_detail_fragment";
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.huodong_framework);
        
        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        InfoFragment fragment = new InfoFragment();
        transation.add(R.id.huodong_container, fragment, HUODONG_FRAGMENT);
        transation.commit();
        
        
        
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

}
