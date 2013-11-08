package com.media.dongfeng.view;

import com.media.dongfeng.MainTabActivity;
import com.media.dongfeng.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BottomTabView extends LinearLayout implements OnClickListener {

    private OnBottomTabChangeListener mListener;
    
    public BottomTabView(Context context) {
        super(context);
        init();
    }

    public BottomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        
    }
    
    @Override
    protected void onLayout( boolean changed, int l, int t, int r, int b ) {
        super.onLayout(changed, l, t, r, b);
        for (int i=0; i<getChildCount(); i++) {
            View child = (View) getChildAt(i);
            child.setOnClickListener(this);
        }
    }

    @Override
    public void onClick( View v ) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btn_sucai_ly) {
            if (mListener.onSelected(R.id.btn_sucai_ly)) {
                getChildAt(0).setBackgroundResource(R.drawable.bottom_bg);
                ImageView sucaiItem = (ImageView)getChildAt(0).findViewById(R.id.btn_sucai);
                sucaiItem.setBackgroundResource(R.drawable.bottom_sucai);
                
                getChildAt(1).setBackgroundResource(R.drawable.bottom_bg);
                ImageView huodongItem = (ImageView)getChildAt(1).findViewById(R.id.btn_huodong);
                huodongItem.setBackgroundResource(R.drawable.bottom_huodong);
                
                getChildAt(2).setBackgroundResource(R.drawable.bottom_bg);
                ImageView shezhiItem = (ImageView)getChildAt(2).findViewById(R.id.btn_shezhi);
                shezhiItem.setBackgroundResource(R.drawable.bottom_shezhi);
                
                sucaiItem.setBackgroundResource(R.drawable.sucai_sel);
                v.setBackgroundResource(R.drawable.bottom_tab_sel_bg);
            }
        } else if (v.getId() == R.id.btn_huodong_ly) {
            if (mListener.onSelected(R.id.btn_huodong_ly)) {
                getChildAt(0).setBackgroundResource(R.drawable.bottom_bg);
                ImageView sucaiItem = (ImageView)getChildAt(0).findViewById(R.id.btn_sucai);
                sucaiItem.setBackgroundResource(R.drawable.bottom_sucai);
                
                getChildAt(1).setBackgroundResource(R.drawable.bottom_bg);
                ImageView huodongItem = (ImageView)getChildAt(1).findViewById(R.id.btn_huodong);
                huodongItem.setBackgroundResource(R.drawable.bottom_huodong);
                
                getChildAt(2).setBackgroundResource(R.drawable.bottom_bg);
                ImageView shezhiItem = (ImageView)getChildAt(2).findViewById(R.id.btn_shezhi);
                shezhiItem.setBackgroundResource(R.drawable.bottom_shezhi);
                
                huodongItem.setBackgroundResource(R.drawable.huodong_sel);
                v.setBackgroundResource(R.drawable.bottom_tab_sel_bg);
            }
        } else if (v.getId() == R.id.btn_shezhi_ly) {
            if (mListener.onSelected(R.id.btn_shezhi_ly)) {
                getChildAt(0).setBackgroundResource(R.drawable.bottom_bg);
                ImageView sucaiItem = (ImageView)getChildAt(0).findViewById(R.id.btn_sucai);
                sucaiItem.setBackgroundResource(R.drawable.bottom_sucai);
                
                getChildAt(1).setBackgroundResource(R.drawable.bottom_bg);
                ImageView huodongItem = (ImageView)getChildAt(1).findViewById(R.id.btn_huodong);
                huodongItem.setBackgroundResource(R.drawable.bottom_huodong);
                
                getChildAt(2).setBackgroundResource(R.drawable.bottom_bg);
                ImageView shezhiItem = (ImageView)getChildAt(2).findViewById(R.id.btn_shezhi);
                shezhiItem.setBackgroundResource(R.drawable.bottom_shezhi);
                
                shezhiItem.setBackgroundResource(R.drawable.shezhi_sel);
                v.setBackgroundResource(R.drawable.bottom_tab_sel_bg);
            }
        }
    }
    
    public void performClickItem(String tab) {
        if (MainTabActivity.SUCAI_TAG.equals(tab)) {
            onClick(getChildAt(0));
        }
        else if (MainTabActivity.HUODONG_TAG.equals(tab)) {
            onClick(getChildAt(1));
        }
        else if (MainTabActivity.SETTING_TAG.equals(tab)) {
            onClick(getChildAt(2));
        }
    }
    
    public void setBottomTabChangeListener (OnBottomTabChangeListener l) {
        this.mListener = l;
    }
    
    public static interface OnBottomTabChangeListener {
        public boolean onSelected(int viewId);
    }
}
