package com.media.dongfeng.exception;

/**
 * Thrown when there were problems parsing the response to an API call,
 * either because the response was empty, or it was malformed.
 */
public class ZhiDaoIOException extends Exception {

    public static final String REASON_HTTPCLIENT = "Fail to Init HttpClient";
    public static final String REASON_SERVER = "Server Error:";
    public static final String REASON_HTTP_METHOD = "Invalid HTTP method";
    public static final String REASON_POST_PARAM = "Unsupported Encoding Exception";
    
    private static final long serialVersionUID = 7729676731472012868L;
    
    /**
     * http请求状态码
     */
    private int statusCode;

    public ZhiDaoIOException() {
        super();
    }

    public ZhiDaoIOException(String detailMessage) {
        super(detailMessage);
    }

    public ZhiDaoIOException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ZhiDaoIOException(Throwable throwable) {
        super(throwable);
    }

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
