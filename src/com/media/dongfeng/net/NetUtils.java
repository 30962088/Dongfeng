package com.media.dongfeng.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.ErrorMessage;
import com.media.dongfeng.utils.Reflection;

public final class NetUtils {
    
    public static int TIMEOUT = 30000;
    public static int SOCKET_BUFFER_SIZE = 8192;
    public static final int REQUEST_TIMEOUT = 2*60*1000;
    public static int HTTP_STATUS_OK = 200;
    
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String TYPE_FILE_NAME = "TYPE_FILE_NAME";
    public static final String GZIP_FILE_NAME = "GZIP_FILE_NAME";
    
    public static final String TAG = "NetUtils";
    /**
     * 应用级别的请求超时计时器
     */
    private static Timer sTimer = new Timer();

    public static class APNWrapper {
        public String name;
        public String apn;
        public String proxy;
        public int port;

        public String getApn() {
            return apn;
        }
        public String getName() {
            return name;
        }
        public int getPort() {
            return port;
        }
        public String getProxy() {
            return proxy;
        }
        APNWrapper() {
        }
    }

    public enum NetworkState {
        NOTHING, MOBILE, WIFI
    }

    private static NetworkState getNetworkState(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        String phoneSystem = getPhoneSystem();
        if (!TextUtils.isEmpty(phoneSystem)
                && (phoneSystem.equals("Ophone OS 2.0") || phoneSystem.equals("OMS2.5"))) {
            if (info == null || !info.isAvailable()) {
                return NetworkState.MOBILE;
            } else {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return NetworkState.MOBILE;
                } else {
                    return NetworkState.WIFI;
                }
            }
        }
        if (info == null || !info.isAvailable()) {
            return NetworkState.NOTHING;
        } else {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NetworkState.MOBILE;
            } else {
                return NetworkState.WIFI;
            }
        }
    }

    public static HttpClient getRequestClient(Context context, String url) throws ZhiDaoIOException {
        HttpClient client = null;
        if (!TextUtils.isEmpty(url) && url.toLowerCase().startsWith("http")) {
            NetUtils.NetworkState state = NetUtils.getNetworkState(context);
            client = new DefaultHttpClient();
            if (state == NetUtils.NetworkState.NOTHING) {
                throw new ZhiDaoIOException("NoSignalException");
            } else if (state == NetUtils.NetworkState.MOBILE) {
                APNWrapper wrapper = null;
                wrapper = getAPN();
                if (!TextUtils.isEmpty(wrapper.proxy)) {
                    client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
                            new HttpHost(wrapper.proxy, wrapper.port));
                }
            }
            HttpConnectionParamBean paramHelper = new HttpConnectionParamBean(client.getParams());
            paramHelper.setSoTimeout(TIMEOUT);
            paramHelper.setConnectionTimeout(TIMEOUT);
            paramHelper.setSocketBufferSize(SOCKET_BUFFER_SIZE);
            return client;
        }
        return client;
    }
    
    private static APNWrapper getAPN() {
        APNWrapper wrapper = new APNWrapper();
        wrapper.proxy = android.net.Proxy.getDefaultHost();
        wrapper.proxy = TextUtils.isEmpty(wrapper.proxy) ? "" : wrapper.proxy;
        wrapper.port = android.net.Proxy.getDefaultPort();
        wrapper.port = wrapper.port > 0 ? wrapper.port : 80;
        return wrapper;
    }
    
    private static String execute(HttpClient client, HttpUriRequest request, Context context)
    		throws ZhiDaoParseException, ZhiDaoApiException, ZhiDaoIOException {
        
        String result = "";
        try {
            result = executeWithoutParse(client, request, context);
        } catch (ZhiDaoIOException e) {
            throw new ZhiDaoIOException(e);
        }
        ErrorMessage err = new ErrorMessage(result);
        /**
         * 没有错误
         */
        if (err == null || err.errno == null || "".equals(err.errno) || "1".equals(err.errno)) {
            return result;
        } else {
            throw new ZhiDaoApiException(err);
        }
    }

    private static String executeWithoutParse(HttpClient client, HttpUriRequest request,
            Context context) throws ZhiDaoIOException {
        
        String url = request.getURI().toString();
        ClientConnectionManager ccm = client.getConnectionManager();
        if (ccm != null && !(ccm instanceof ThreadSafeClientConnManager)) {
            try {
                if (request instanceof HttpGet) {
                    ((HttpGet) request).setURI(new URI(url));
                } else if (request instanceof HttpPost) {
                    ((HttpPost) request).setURI(new URI(url));
                }
            } catch (URISyntaxException e) {
                throw new ZhiDaoIOException(e);
            }
        }
        
        Log.d(TAG, "url:"+url);
        request.setHeader("Accept-Encoding", "gzip,deflate"); 
        RequestWrapper wrapper = new RequestWrapper(request);
        TimerTask task = getRequestTimerTask(wrapper);
        try {
            HttpResponse response = null;
            try {
                sTimer.schedule(task, REQUEST_TIMEOUT);
                response = client.execute(request);
                wrapper.isFinish = true;
            } catch (NullPointerException e) {
                task.cancel();
                task = getRequestTimerTask(wrapper);
                sTimer.schedule(task, REQUEST_TIMEOUT);
                try {
                    response = client.execute(request);
                    wrapper.isFinish = true;
                } catch (NullPointerException e1) {
                    throw new ZhiDaoIOException(e1);
                } finally {
                    task.cancel();
                    sTimer.purge();
                }
            } finally {
                // 请求正常返回,取消计时,并且清除已取消计时任务
                task.cancel();
                sTimer.purge();
            }

            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            if (statusCode != HTTP_STATUS_OK) {
            	ZhiDaoIOException ex = new ZhiDaoIOException(status.toString());
            	ex.setStatusCode(statusCode);
            	throw ex;
            }
            // Pull content stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            ByteArrayOutputStream content = new ByteArrayOutputStream();

            Header header = response.getFirstHeader("Content-Encoding");
            if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
                inputStream = new GZIPInputStream(inputStream);
            }
            // Read response into a buffered stream
            int readBytes = 0;
            byte[] sBuffer = new byte[512];
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            // Return result from buffered stream
            String result = new String(content.toByteArray());
            Log.d(TAG, "result:"+result);
            return result;
        } catch (IOException e) {
            throw new ZhiDaoIOException(e);
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
    }

    public static String openUrl(Context context, String url, String method, Bundle params) throws ZhiDaoParseException,
            ZhiDaoApiException, ZhiDaoIOException {

        HttpClient client = null;
        client = getRequestClient(context, url);
        StringBuilder newUrl = new StringBuilder();
        String response = "";

        if (NetUtils.METHOD_GET.equals(method)) {
            newUrl.append(getCompleteUrl(url, params));
            HttpGet request = new HttpGet(newUrl.toString());
            response = execute(client, request, context);
            return response;
            
        } else if (NetUtils.METHOD_POST.equals(method)) {
            MultipartEntity multipartContent = buildMultipartEntity(params);
            newUrl.append(url);
            HttpPost request = new HttpPost(newUrl.toString());
            request.setEntity(multipartContent);
            response = execute(client, request, context);
            return response;
        } else {
            throw new ZhiDaoIOException(ZhiDaoIOException.REASON_HTTP_METHOD);
        }
    }

    private static String getCompleteUrl(String url, Bundle getParams) {
        StringBuilder newUrl = new StringBuilder();
        String[] items = url.split("\\?");
        if (items.length == 2) {
            newUrl.append(items[0]).append("?");
            String array[] = items[1].split("&");
            boolean first = true;
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (first) {
                    first = false;
                } else {
                    newUrl.append("&");
                }
                if (v.length == 2) {
                    newUrl.append(URLEncoder.encode(v[0])).append("=")
                            .append(URLEncoder.encode(v[1]));
                } else {
                    newUrl.append(parameter);
                }

            }
            if (getParams != null) {
                newUrl.append(encodeUrl(getParams));
            }
        } else {
            newUrl.append(url).append("?").append(encodeUrl(getParams));
        }
        return newUrl.toString();
    }

    private static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(urlEncode(key) + "=" + urlEncode(parameters.getString(key)));
        }
        return sb.toString();
    }
    
    private static String urlEncode(String in) {
        if (TextUtils.isEmpty(in)) {
            return "";
        }
        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    
    /**
     * 把Bundle转化为MultipartEntity
     * @param params
     * @return
     * @throws ZhiDaoIOException
     */
    private static MultipartEntity buildMultipartEntity(Bundle params) throws ZhiDaoIOException {

        MultipartEntity multipartContent = null;
        multipartContent = new MultipartEntity();
        
        for (String key : params.keySet()) {
            if (TYPE_FILE_NAME.equals(key) || GZIP_FILE_NAME.equals(key)) {
                Object fileNames = params.get(key);
                if (fileNames != null && fileNames instanceof Bundle) {
                    Bundle pathBundle = (Bundle) fileNames;
                    for (String uploadFileKey : pathBundle.keySet()) {
                        final File file = new File(pathBundle.getString(uploadFileKey));
                        if (file.exists()) {
                            FileBody bin;
                            if (TYPE_FILE_NAME.equals(key)) {
                                bin = new FileBody(file, "image/jpeg");
                            } else {                           	
                                bin = new FileBody(file, "application/zip");
                            }
                            multipartContent.addPart(uploadFileKey, bin);
                        }
                    }
                }
            } else {
            	Object objValue = params.get(key);
            	if(objValue != null && objValue instanceof byte[]){
            		byte[] bytesValue = (byte[]) objValue;
            		ByteArrayBody body = new ByteArrayBody(bytesValue, null);
            		multipartContent.addPart(key, body);
            	}else{
	                StringBody sb1;
	                try {  
	                    String value = params.getString(key);
	                    value = (value == null ? "" : value);
	                    Log.d(TAG, "StringBody key:"+key+" value:"+value);
	                	sb1 = new StringBody(value, Charset.forName(HTTP.UTF_8)); 
	                	multipartContent.addPart(URLEncoder.encode(key), sb1);
	                } catch (UnsupportedEncodingException e) {
	                    throw new ZhiDaoIOException(e);
	                }
            	}
            }
        }
        Log.d(TAG, "StringBody end--------------");
        return multipartContent;
    }

    // for ophone system
    private static Reflection reflection;

    private static String getPhoneSystem() {
        if (reflection == null) {
            reflection = new Reflection();
        }
        try {
            Object opp = reflection.newInstance("android.os.SystemProperties", new Object[] {});
            String system = (String) reflection.invokeMethod(opp, "get", new Object[] {
                    "apps.setting.platformversion", "" });
            return system;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 判断当前网络是否为wifi
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private static class RequestWrapper {

        private HttpUriRequest mRequest;

        private boolean isFinish;

        public RequestWrapper(HttpUriRequest request) {
            mRequest = request;
            isFinish = false;
        }
    }

    /**
     * 得到请求的超时计时器
     * 
     * @param wrapper
     * @return
     */
    private static TimerTask getRequestTimerTask(final RequestWrapper wrapper) {

        return new TimerTask() {

            @Override
            public void run() {

                if (wrapper != null && wrapper.mRequest != null && !wrapper.isFinish) {
                    wrapper.mRequest.abort();
                }
            }
        };
    }
    
}
