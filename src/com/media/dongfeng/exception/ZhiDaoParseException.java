package com.media.dongfeng.exception;

/**
 * Thrown when there were problems parsing the response to an API call,
 * either because the response was empty, or it was malformed.
 */
public class ZhiDaoParseException extends Exception {

    private static final long serialVersionUID = 3132128578218204998L;

    public ZhiDaoParseException() {
        super();
    }

    public ZhiDaoParseException(String detailMessage) {
        super(detailMessage);
    }

    public ZhiDaoParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ZhiDaoParseException(Throwable throwable) {
        super(throwable);
    }

}
