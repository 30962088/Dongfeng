package com.media.dongfeng.model;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.media.dongfeng.exception.ZhiDaoParseException;

public class User implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7725393194326885950L;
    
    public String mid;
    public String name;
    public String media;
    public String email;
    public String pkey="";
    
    public User() {
        
    }
    
    public User(String json) throws ZhiDaoParseException {
        if(TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(json);
            int uid = obj.optInt("mid", -1);
            if (uid > 0) {
                mid = String.valueOf(uid);
            }
            pkey = obj.getString("Key");
        } catch (JSONException e) {
            throw new ZhiDaoParseException(e);
        }
    }

}
