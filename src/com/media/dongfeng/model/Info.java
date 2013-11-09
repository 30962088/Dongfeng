package com.media.dongfeng.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.media.dongfeng.R;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.Content.Size;

public class Info implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5383147808205010263L;
    public int iid;
    public String title;
    public String description;
    public String imageurl;
    public String imgformat;
    public String datetime;
    public int imgWidth;
    public int imgHeight;
    public boolean isRead;
    
    public Info() {
		// TODO Auto-generated constructor stub
	}

    public Info(String json) throws ZhiDaoParseException {
        if(TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(json);
            iid = obj.optInt("iid");
            title = obj.optString("title");
            description = obj.optString("description");
            imageurl = obj.optString("imageurl");
            imgformat = obj.optString("imgformat");
            datetime = obj.optString("datetime");
            isRead = obj.optBoolean("isRead");
            imgWidth = obj.getInt("imgwidth");
			imgHeight = obj.getInt("imgheight");
//            isRead = false;
        } catch (JSONException e) {
            throw new ZhiDaoParseException(e);
        }
    }
    
    public Size getSize(Context context) {
		Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		int height = context.getResources().getDimensionPixelSize(
				R.dimen.detail_banner_height);
		if (imgWidth >= imgHeight) {
			height = (int) Math.round((imgHeight * width * 1.0 / imgWidth));
		} else {
			width = (int) Math.round((imgWidth * height * 1.0 / imgHeight));
		}
		return new Size(width, height);

	}

}
