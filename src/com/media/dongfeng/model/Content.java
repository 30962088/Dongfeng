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

public class Content implements Serializable {

	public static class Size {
		public int width;
		public int height;

		public Size(int width, int height) {
			super();
			this.width = width;
			this.height = height;
		}

	}

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
	public int imgWidth;
	public int imgHeight;
	public String datetime;
	public boolean isJoined;
	public boolean isRead;
	public boolean isTop = false;
	public boolean isCatTitle = false;

	public Content() {
		// TODO Auto-generated constructor stub
	}

	public Content(String json) throws ZhiDaoParseException {
		if (TextUtils.isEmpty(json)) {
			return;
		}
		try {
			JSONObject obj = new JSONObject(json);
			isTop = obj.getBoolean("isTop");
			cid = obj.optInt("cid");
			cfid = obj.getInt("cfid");
			title = obj.optString("title");
			description = obj.optString("description");
			imageurl = obj.optString("imageurl");
			imgguid = obj.optString("imgguid");
			fileformat = obj.optString("fileformat");
			datetime = obj.optString("datetime");
			isJoined = obj.optBoolean("isJoined");
			// isRead = false;
			imgWidth = obj.getInt("imgWidth");
			imgHeight = obj.getInt("imgHeight");
			isRead = obj.optBoolean("isRead");

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
