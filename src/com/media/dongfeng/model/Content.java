package com.media.dongfeng.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.media.dongfeng.exception.ZhiDaoParseException;

public class Content implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5383147808205010263L;
    public int cid;
    public int cfid;
    public String title;
    public String description;
    public String imageurl;
    public String imgguid;
    public String fileformat;
    public String datetime;
    public boolean isJoined;
    public boolean isRead;
    public boolean isCatTitle = false;
    
    public Content() {
		// TODO Auto-generated constructor stub
	}

    public Content(String json) throws ZhiDaoParseException {
        if(TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(json);
            cid = obj.optInt("cid");
            cfid = obj.getInt("cfid");
            title = obj.optString("title");
            description = obj.optString("description");
            imageurl = obj.optString("imageurl");
            imgguid = obj.optString("imgguid");
            fileformat = obj.optString("fileformat");
            datetime = obj.optString("datetime");
            isJoined = obj.optBoolean("isJoined");
            isRead = obj.optBoolean("isRead");
            
        } catch (JSONException e) {
            throw new ZhiDaoParseException(e);
        }
    }

}
