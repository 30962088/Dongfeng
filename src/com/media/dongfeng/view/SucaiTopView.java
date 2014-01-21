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
import com.media.dongfeng.model.Content.Size;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.BmpCache;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.MD5;

public class SucaiTopView extends RelativeLayout {

    public ImageView mIcon;
    public TextView mTitle;
    private Content mContent;
    public ImageView mCornIcon;
    private ImageView mType;
    private static Drawable sNewIconDrawable;
    private static int sItemIconWidth = -1;
    private static int sItemIconHeight = -1;
    
    public SucaiTopView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }

    public SucaiTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }

    public SucaiTopView(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.info_top_layout, this);
        mIcon = (ImageView) findViewById(R.id.img);
        mTitle = (TextView) findViewById(R.id.title);
        mCornIcon = (ImageView) findViewById(R.id.new_icon);
        mType = (ImageView) findViewById(R.id.type);
    }
    
    public void update(Content content, int bgColor) {
        this.mContent = content;
        setBackgroundColor(bgColor);
        mTitle.setText(content.title);
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
        if(content.cfid>0){
        	mType.setImageDrawable(getResources().getDrawable(R.drawable.folder_small));
        }
        if(content.cfid>0&&content.cid>0){
        	mType.setImageDrawable(getResources().getDrawable(R.drawable.arrow));
        }
        Size size = content.getSize(getContext());
        loadPicture(mIcon, mContent.imageurl, size.width, size.height);
    }
    
    private void loadPicture (ImageView view, String picUrl, int width, int height) {
        String key = getKey(picUrl, width, height);
        Bitmap bmp = BmpCache.getInstance().get(key);
        if (bmp != null && !bmp.isRecycled()) {
            view.setImageBitmap(bmp);
            return;
        }
        try {
            new LoadPictureTask(view,  picUrl, width, height).execute();
        } catch (RejectedExecutionException e) {
        }
    } 
    
    private class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {

        private String mPicUrl;
        private int mWidth;
        private int mHeight;
        private ImageView view;
        
        public LoadPictureTask(ImageView view, String picUrl, int width, int height) {
            this.view = view;
            this.mPicUrl = picUrl;
            this.mWidth = width;
            this.mHeight = height;
        }

        protected Bitmap doInBackground( Void... args ) {
            Bitmap bmp = null;
            String file = null;
            try {
                file = NetDataSource.getInstance(getContext()).getImage(mPicUrl, mWidth, mHeight, Constants.SAVE_PIC_PATH);
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
        	view.setImageBitmap(bmp);
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
