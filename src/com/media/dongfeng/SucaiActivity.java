package com.media.dongfeng;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;


public class SucaiActivity extends BaseActivity {

    public static final String SUCAI_FRAGMENT = "sucai_fragment";
    public static final String SUCAI_DETAIL_FRAGMENT = "sucai_detail_fragment";
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sucai_framework);
        
        FragmentTransaction transation = getSupportFragmentManager().beginTransaction();
        SucaiFragment fragment = new SucaiFragment();
        transation.add(R.id.sucai_container, fragment, SUCAI_FRAGMENT);
        transation.commit();
        
        
        
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

}
