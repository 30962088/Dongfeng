package com.media.dongfeng.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.ContentList;
import com.media.dongfeng.model.InfoList;
import com.media.dongfeng.model.User;
import com.media.dongfeng.utils.MD5;
import com.media.dongfeng.utils.Utils;

public class NetDataSource {

    private static final String TAG = "dongfeng";
    
    private static NetDataSource sInstance;
    private Context mContext;
    
    public static final String HOST = "http://df.1du1du.com/get.mvc/";
    
    public static final String HOST_2 = "http://42.121.113.199:83/get.mvc/";
    
    private String android_id;
    
    private NetDataSource(Context context) {
        mContext = context;
        android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
    
    public static synchronized NetDataSource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetDataSource(context);
        }
        return sInstance;
    }
    
    /**
     * 登录
     * @return
     * @throws ZhiDaoParseException
     * @throws ZhiDaoApiException
     * @throws ZhiDaoIOException
     */
    public User login(String name, String company, String email) 
            throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        Bundle param = new Bundle();
        param.putString("name", name);
        param.putString("company", company);
        param.putString("email", email);
        param.putString("deviceid", android_id);
        param.putString("pkey", "");
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("PersonLogin");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "login = "+content);
        return new User(content);
    }
    
    /**
     * 素材列表接口
     * @param user
     * @param type 0:素材1：活动
     * @param size
     * @param page
     * @param keyword
     * @return
     * @throws ZhiDaoParseException
     * @throws ZhiDaoApiException
     * @throws ZhiDaoIOException
     */
    public ContentList getContentsList (User user,int cfid, int type, int size, int page, String keyword) 
            throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        Log.d("net", "getContentsList type="+type+"   size="+size+"    page="+page+"    keyword="+keyword);
        Bundle param = new Bundle();
        if (user != null && !TextUtils.isEmpty(user.mid)) {
            param.putString("mid", String.valueOf(user.mid));
        }
        param.putString("cfid", ""+cfid);
//        param.putString("type", String.valueOf(type));
        param.putString("size", String.valueOf(size));
        param.putString("page", String.valueOf(page));
        if (keyword == null) {
            param.putString("keyword", "");
        } else {
            param.putString("keyword", keyword);
        }
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("getcontentsList");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "getContentsList="+content);
        return new ContentList(content);
    }
    
    
    public InfoList getInfoList(User user,int page,int size,String keyword) throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException{
        Bundle param = new Bundle();
        if (user != null && !TextUtils.isEmpty(user.mid)) {
            param.putString("mid", String.valueOf(user.mid));
        }
//        param.putString("type", String.valueOf(type));
        param.putString("size", String.valueOf(size));
        param.putString("page", String.valueOf(page));
        if (keyword == null) {
            param.putString("keyword", "");
        } else {
            param.putString("keyword", keyword);
        }
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("GetInfoList");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "getContentsList="+content);
        return new InfoList(content);
    }
    
    /**
     * 用户参加活动接口
     * @param user
     * @param cid
     * @return
     * @throws ZhiDaoParseException
     * @throws ZhiDaoApiException
     * @throws ZhiDaoIOException
     */
    public boolean JoinActivities(User user, int cid) throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        Bundle param = new Bundle();
        if (user != null && !TextUtils.isEmpty(user.mid)) {
            param.putString("mid", String.valueOf(user.mid));
        }
        param.putString("cid", String.valueOf(cid));
        param.putString("type", "0");
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("JoinActivities");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "JoinActivities="+content);
        return checkReturnBoolean(content);
    }
    
    /**
     * 用户已读
     * @param user
     * @param cid
     * @return
     * @throws ZhiDaoParseException
     * @throws ZhiDaoApiException
     * @throws ZhiDaoIOException
     */
    public boolean ReadSucai(User user, int cid,int type) throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        Bundle param = new Bundle();
        if (user != null && !TextUtils.isEmpty(user.mid)) {
            param.putString("mid", String.valueOf(user.mid));
        }
        param.putString("id", String.valueOf(cid));
        param.putString("type", ""+type);
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("ReadInfoMaterial");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "JoinActivities="+content);
        return checkReturnBoolean(content);
    }
    
    /**
     * 发送邮件接口
     * @param user
     * @param cid
     * @return
     * @throws ZhiDaoParseException
     * @throws ZhiDaoApiException
     * @throws ZhiDaoIOException
     */
    public boolean sendMail(User user, int cid,int type) throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        Bundle param = new Bundle();
        if (user != null && !TextUtils.isEmpty(user.mid)) {
            param.putString("mid", String.valueOf(user.mid));
        }
        param.putString("id", String.valueOf(cid));
        param.putString("type", ""+type);
        StringBuilder url = new StringBuilder();
        url.append(HOST).append("SendEmail");
        String content = NetUtils.openUrl(mContext, url.toString(), "GET", param);
        Log.d(TAG, "sendemail="+content);
        return checkReturnBoolean(content);
    }
    
    public String getImage (String url, int width, int height, String savedir) throws ZhiDaoIOException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String filepath = savedir + "/" + MD5.hexdigest(url);
        File file = new File(filepath);
        
        String tempFilepath = filepath + "_" + System.currentTimeMillis();
        Utils.makeFilepathExsist(tempFilepath);

        if (file.exists()) {
            if (file.length() > 0) {//文件已存在并且长度不为0则返回
//                DownloadManager.getInstance().remove(filepath);
                return filepath;
            } else {
                file.delete();///
            }
        }
        
        File tempFile = new File(tempFilepath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        
        if(width > 0 && height > 0){
        	if(url.indexOf("?") == -1){
        		url += "?";
        	}else{
        		url += "&";
        	}
        	url+="width="+width+"&height="+height;
        }
        
        HttpGet request = new HttpGet(url);
        HttpClient client = NetUtils.getRequestClient(mContext, url);
        HttpResponse response = null;
        InputStream inputStream = null;
        FileOutputStream content = null;
        try{
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            if (NetUtils.HTTP_STATUS_OK != status.getStatusCode()) {
                ZhiDaoIOException ex = new ZhiDaoIOException(
                        String.format("Invalid response from server: %s", status.toString()));
                throw ex;
            }
            final int len = 4096;
            byte[]  buf = new byte[len];
            int readNums = 0;
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            content = new FileOutputStream(tempFilepath, true);
            while((readNums = inputStream.read(buf, 0, len)) != -1) {
                content.write(buf, 0, readNums);
            }
            content.flush();
            tempFile.renameTo(file);
            return filepath;
            
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (file.exists()) {
                file.delete();
            }
            return "";
        } finally {
            if (content != null) {
                try {
                    content.close();
                } catch (IOException e1) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
//            DownloadManager.getInstance().remove(filepath);
        }
    }
    
    private boolean checkReturnBoolean(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        if (content.indexOf("\"yes\"") > -1) {
            return true;
        }
        return false;
    }
    
}
