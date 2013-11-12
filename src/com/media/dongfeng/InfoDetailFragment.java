package com.media.dongfeng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.Content;
import com.media.dongfeng.model.Info;
import com.media.dongfeng.model.User;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.Utils;

public class InfoDetailFragment extends Fragment {

    private TextView mBackBtn;
    private ImageView mAnniu;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mContentView;
    private ImageView mDetailBannerView;
    
    private Info mContent;
    
    private List<Info> mSucaiList = new ArrayList<Info>();
    
    private boolean mSendMailTaskFree;
    
    public InfoDetailFragment() {
		// TODO Auto-generated constructor stub
	}
    
    public InfoDetailFragment (Info content) {
        this.mContent = content;
        if(!mContent.isRead){
        	mContent.isRead = true;
        	Read(MainTabActivity.mUser, content.iid);
        }
        
        
    }
    
    private void Read(User user, int cid) {
        if (user != null) {
            new ReadTask(user, cid).execute();
        }
    }
    
    private class ReadTask extends AsyncTask<Void, Void, Void> {

        private User mUser;
        private int mCid;
        
        public ReadTask(User user, int cid) {
            this.mUser = user;
            this.mCid = cid;
        }

        protected Void doInBackground( Void... args ) {
            try {
                NetDataSource.getInstance(getActivity()).ReadSucai(this.mUser, this.mCid,2);
            } catch (ZhiDaoIOException e) {
            } catch (ZhiDaoApiException e) {
            } catch (ZhiDaoParseException e) {
                
            }
            return null;
        }
    }
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated(savedInstanceState);
        mSendMailTaskFree = true;
        
        if (MainTabActivity.mUser != null) {
            mSucaiList = Utils.loadInfoCidList(getActivity(), MainTabActivity.mUser);
        }
        
        
        mBackBtn = (TextView) getView().findViewById(R.id.back);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                // TODO Auto-generated method stub
                getFragmentManager().popBackStack();
            }
        });
        
        mAnniu = (ImageView) getView().findViewById(R.id.anniu);
        mAnniu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                
                sendMail(MainTabActivity.mUser, mContent.iid);
                
            }
        });
//        updateHuodongButtonBg(mIsSucai, hasJoint(mIsSucai, mContent));
        
        mTitleView = (TextView) getView().findViewById(R.id.titleTxt);
        mTitleView.setText(mContent.title);
        mTimeView = (TextView) getView().findViewById(R.id.timeTxt);
        mTimeView.setText(formateDate(mContent.datetime));
        mContentView = (TextView) getView().findViewById(R.id.contentTxt);
        mContentView.setText(mContent.description);
        
        mDetailBannerView = (ImageView) getView().findViewById(R.id.detail_banner);
//        ViewGroup.LayoutParams lp = mDetailBannerView.getLayoutParams();
//        lp.width = getDisplayWidth();
//        mDetailBannerView.setLayoutParams(lp);
        
        loadPicture(mDetailBannerView, mContent.imageurl, getDisplayWidth(), 
                getActivity().getResources().getDimensionPixelSize(R.dimen.detail_banner_height));
    }
    
    
    
    
    @Override
    public void onPause() {
        super.onPause();
       
        if (MainTabActivity.mUser != null) {
            Utils.saveInfoCidList(getActivity(), MainTabActivity.mUser, mSucaiList);
        }
        
    }

    private int getDisplayWidth() {
        DisplayMetrics  dm = new DisplayMetrics();  
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);  
        return dm.widthPixels;  
    }
    
    public static String formateDate(String date) {
        if (!TextUtils.isEmpty(date)) {
            int start = date.indexOf("(");
            int end = date.indexOf(")");
            String dateStr = date.substring(start+1, end);
            try {
                long dateL = Long.valueOf(dateStr);
                Date d = new Date(dateL);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                return format.format(d);
            } catch (Exception e) {}
        }
        return "";
    }
    
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        return inflater.inflate(R.layout.sucai_huodong_detail_layout, null);
    }
    
    
    
    
    private void sendMail(User user, int cid) {
        if (user != null) {
            if (!mSendMailTaskFree) {
                return;
            } 
            mSendMailTaskFree = false;
            new SendMailTask(user, cid).execute();
        }
    }
    
    private class SendMailTask extends AsyncTask<Void, Void, Boolean> {

        private User mUser;
        private int mCid;
        
        public SendMailTask(User user, int cid) {
            this.mUser = user;
            this.mCid = cid;
        }

        protected Boolean doInBackground( Void... args ) {
            try {
                if (NetDataSource.getInstance(getActivity()).sendMail(mUser, mCid,2)) {
                    return true;
                }
            } catch (ZhiDaoIOException e) {
            } catch (ZhiDaoApiException e) {
            } catch (ZhiDaoParseException e) {
                
            }
            return false;
        }
        
        @Override
        protected void onPostExecute( Boolean result ) {
            super.onPostExecute(result);
            if (result) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "发送成功", 0).show();
                }
            } else {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "发送失败", 0).show();
                }
            }
            mSendMailTaskFree = true;
        }
    }
    
    
    private void loadPicture (ImageView view, String picUrl, int width, int height) {
        new LoadPictureTask(picUrl, width, height).execute();
    } 
    
    private class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {

        private String mPicUrl;
        private int mWidth;
        private int mHeight;
        
        public LoadPictureTask(String picUrl, int width, int height) {
            this.mPicUrl = picUrl;
            this.mWidth = width;
            this.mHeight = height;
        }

        protected Bitmap doInBackground( Void... args ) {
            Log.d("test", "detail doInBackground");
            Bitmap bmp = null;
            String file = null;
            try {
                file = NetDataSource.getInstance(getActivity()).getImage(mPicUrl, mWidth, mHeight, Constants.SAVE_PIC_PATH);
                Log.d("test", "detail file");
            } catch (ZhiDaoIOException e) {
                return null;
            }
            try {
                if (TextUtils.isEmpty(file) || !new File(file).exists()) {
                    Log.d("test", "detail file null");
                    return null;
                }
                bmp = BitmapFactory.decodeFile(file);
            } catch (OutOfMemoryError e) {
                System.gc();
            }
            return bmp;
        }

        protected void onPostExecute( Bitmap bmp ) {
            Log.d("test", "detail onPostExecute");
            if (bmp == null || bmp.isRecycled()) {
                Log.d("test", "detail onPostExecute bmp is nulll");
                return;
            }
            Log.d("test", "detail onPostExecute bmp not nulll");
            mDetailBannerView.setImageBitmap(bmp);
            /**
            int ivWidth = mWidth;
            int ivHeight = mHeight;
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            if (ivWidth > bmpWidth && ivHeight > bmpHeight) {
                mDetailBannerView.setImageBitmap(bmp);
            } else {
                if (ivWidth > bmpWidth) {
                    float scaleHeight = (float)ivHeight / bmpHeight + 0.5f;
                    int dstHeight = mHeight;
                    int dstWidth = (int)((float)bmpWidth * scaleHeight + 0.5f);
                    Bitmap dstBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
                    bmp.recycle();
                    if (dstBmp != null && dstBmp.isRecycled()) {
                        mDetailBannerView.setImageBitmap(dstBmp);
                    }
                } else if (ivHeight > bmpHeight) {
                    float scaleWidth = (float)ivWidth / bmpWidth + 0.5f;
                    int dstWidth = mWidth;
                    int dstHeight = (int)((float)bmpHeight * scaleWidth + 0.5f);
                    Bitmap dstBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
                    bmp.recycle();
                    if (dstBmp != null && dstBmp.isRecycled()) {
                        mDetailBannerView.setImageBitmap(dstBmp);
                    }
                } else {
                    float scaleWidth = (float)ivWidth / bmpWidth + 0.5f;
                    float scaleHeight = (float)ivHeight / bmpHeight + 0.5f;
                    float scale = Math.min(scaleWidth, scaleHeight);
                    int dstWidth = (int)(bmpWidth * scale + 0.5f);
                    int dstHeight = (int)(bmpHeight * scale + 0.5f);
                    Bitmap dstBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, false);
                    bmp.recycle();
                    if (dstBmp != null && dstBmp.isRecycled()) {
                        mDetailBannerView.setImageBitmap(dstBmp);
                    }
                }
            }
            **/
        }
    }
    
    private class CustomDialog extends Dialog {
        public CustomDialog(Context context, int theme) {
            super(context, theme);
            // TODO Auto-generated constructor stub
        }
        
        private void windowDeploy(int x, int y) {
            Window window = getWindow(); // 得到对话框
            window.setWindowAnimations(R.style.ChoiceDialogAnim); // 设置窗口弹出动画
            // window.setBackgroundDrawableResource(R.color.vifrification);
            // //设置对话框背景为透明
            WindowManager.LayoutParams wl = window.getAttributes();
            // 根据x，y坐标设置窗口需要显示的位置
            wl.x = x; // x小于0左移，大于0右移
            wl.y = y; // y小于0上移，大于0下移
            // wl.alpha = 0.6f; //设置透明度
            wl.gravity = Gravity.BOTTOM; // 设置重力
//          window.setAttributes(wl);
            onWindowAttributesChanged(wl);
        }
        
        public void showDialog() {
            setContentView(R.layout.dialog_layout);
            findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    // TODO Auto-generated method stub
                    if (isShowing()) {
                        dismiss();
                    }
                }
            });
            windowDeploy(0, 0);
            // 设置触摸对话框意外的地方取消对话框
            setCanceledOnTouchOutside(false);
            show();
        }
    }
}
