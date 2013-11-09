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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.media.dongfeng.R;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.model.Content;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.BmpCache;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.MD5;

public class CatTitleView extends RelativeLayout {

	public static interface OnDianjiClick{
		public void onclick(View view);
	}
	
    public ImageView mIcon;
    public TextView mTitle;
    public View mDianji;
    private OnDianjiClick onDianjiClick;
//    public TextView mDesc;
//    public ImageView mCornIcon;

    private Content mContent;
    
    public CatTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }

    public CatTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }
    
    public void setOnDianjiClick(OnDianjiClick onDianjiClick) {
		this.onDianjiClick = onDianjiClick;
	}

    public CatTitleView(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.cat_title_item_layout, this);
        mIcon = (ImageView) findViewById(R.id.img);
        mTitle = (TextView) findViewById(R.id.title);
        mDianji = findViewById(R.id.dianji);
        mDianji.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(onDianjiClick != null){
					onDianjiClick.onclick(mDianji);
				}
				
			}
		});
    }
    
    public void update(Content content) {
        this.mContent = content;
        mTitle.setText(content.title);
        loadPicture(mIcon, mContent.cid, mContent.imageurl);
    }
    
    private void loadPicture (ImageView view, int cid, String picUrl) {
        String key = getKey(picUrl, 0, 0);
        Bitmap bmp = BmpCache.getInstance().get(key);
        if (bmp != null && !bmp.isRecycled()) {
            view.setImageBitmap(bmp);
            return;
        }
        try {
            new LoadPictureTask(view, cid, picUrl).execute();
        } catch (RejectedExecutionException e) {
        }
    } 
    
    private class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {

        private String mPicUrl;
        private int mCid;
        private ImageView view;
        
        public LoadPictureTask(ImageView view, int cid, String picUrl) {
            this.view = view;
            this.mPicUrl = picUrl;
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
//            if (mCid != mContent.cid) {
//               return; 
//            }
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
