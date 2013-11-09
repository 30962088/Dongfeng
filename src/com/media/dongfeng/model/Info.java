package com.media.dongfeng.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.media.dongfeng.exception.ZhiDaoParseException;

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
//            isRead = false;
        } catch (JSONException e) {
            throw new ZhiDaoParseException(e);
        }
    }

}
