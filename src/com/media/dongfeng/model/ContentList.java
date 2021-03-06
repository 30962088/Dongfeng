package com.media.dongfeng.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.media.dongfeng.exception.ZhiDaoParseException;

public class ContentList {

    public List<Content> mContentList = new ArrayList<Content>();

    public ContentList(String json) throws ZhiDaoParseException {
        if(TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONArray jsonArr = new JSONArray(json);
            for (int i=0; i<jsonArr.length(); i++) {
                JSONObject obj = jsonArr.optJSONObject(i);
                mContentList.add(new Content(obj.toString()));
            }
        } catch (JSONException e) {
            throw new ZhiDaoParseException(e);
        }
    }

}
