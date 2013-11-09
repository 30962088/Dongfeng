package com.media.dongfeng.view;

import java.io.File;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.media.dongfeng.R;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.model.Content;
import com.media.dongfeng.model.Info;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.BmpCache;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.MD5;

public class InfoView extends RelativeLayout {

    public ImageView mIcon;
    public TextView mTitle;
    public TextView mDesc;
    public ImageView mCornIcon;

    private Info mContent;
    
    private static Drawable sNewIconDrawable;
    private static int sItemIconWidth = -1;
    private static int sItemIconHeight = -1;
    
    public InfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }

    public InfoView(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_view_layout, this);
        mIcon = (ImageView) findViewById(R.id.item_icon);
        mTitle = (TextView) findViewById(R.id.title);
        mDesc = (TextView) findViewById(R.id.desc);
        mCornIcon = (ImageView) findViewById(R.id.right_corn_icon);
    }
    
    public void update(Info content, int bgColor) {
        this.mContent = content;
        setBackgroundColor(bgColor);
        mIcon.setBackgroundColor(bgColor);
        mTitle.setText(content.title);
        mDesc.setText(content.description);
        if (content.isRead) {
            mCornIcon.setImageDrawable(null);
        } else {
            if (sNewIconDrawable == null) {
                sNewIconDrawable = getResources().getDrawable(R.drawable.new_icon);
            }
            mCornIcon.setImageDrawable(sNewIconDrawable);
        }
        
        if (sItemIconWidth < 0) {
            sItemIconWidth = getContext().getResources().getDimensionPixelSize(R.dimen.item_icon_width);
        }
        if (sItemIconHeight < 0) {
            sItemIconHeight = getContext().getResources().getDimensionPixelSize(R.dimen.item_icon_height);
        }
        loadPicture(mIcon, mContent.iid, mContent.imageurl, sItemIconWidth, sItemIconHeight);
    }
    
    private void loadPicture (ImageView view, int cid, String picUrl, int width, int height) {
        String key = getKey(picUrl, width, height);
        Bitmap bmp = BmpCache.getInstance().get(key);
        if (bmp != null && !bmp.isRecycled()) {
            view.setImageBitmap(bmp);
            return;
        }
        try {
            new LoadPictureTask(view, cid, picUrl, width, height).execute();
        } catch (RejectedExecutionException e) {
        }
    } 
    
    private class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {

        private String mPicUrl;
        private int mWidth;
        private int mHeight;
        private int mCid;
        private ImageView view;
        
        public LoadPictureTask(ImageView view, int cid, String picUrl, int width, int height) {
            this.view = view;
            this.mPicUrl = picUrl;
            this.mWidth = width;
            this.mHeight = height;
            this.mCid = cid;
        }

        protected Bitmap doInBackground( Void... args ) {
            Bitmap bmp = null;
            String file = null;
            try {
                file = NetDataSource.getInstance(getContext()).getImage(mPicUrl, -1, -1, Constants.SAVE_PIC_PATH);
            } catch (ZhiDaoIOException e) {
                return null;
            }
            try {
                if (TextUtils.isEmpty(file) || !new File(file).exists()) {
                    return null;
                }
                bmp = BitmapFactory.decodeFile(file);
                String key = getKey(mPicUrl, -1, -1);
                BmpCache.getInstance().save(key, bmp);
            } catch (OutOfMemoryError e) {
                System.gc();
            }
            return bmp;
        }

        protected void onPostExecute( Bitmap bmp ) {
            if (mCid != mContent.iid) {
               return; 
            }
            mIcon.setImageBitmap(bmp);
        }
    }
    
    public static final String getKey(String picUrl, int width, int height) {
        /**
        StringBuilder url = new StringBuilder(picUrl);
        return url.toString();
        **/
        return MD5.hexdigest(picUrl);
    }
}
