package com.media.dongfeng.exception;

import com.media.dongfeng.model.ErrorMessage;

public class ZhiDaoApiException extends Exception {

    private static final long serialVersionUID = -5143101071713313135L;
    
    
    //错误信息
    private ErrorMessage mErrMessage;
    

    public ErrorMessage getErrMessage() {
        return mErrMessage;
    }


    public ZhiDaoApiException() {
        super();
    }

    public ZhiDaoApiException(String detailMessage) {
        super(detailMessage);
    }
    
    public ZhiDaoApiException(ErrorMessage err) {
        super("Error Code:" + err.errno + ",Reason:"
                + err.errmsg);
        mErrMessage = err;
    }

    public ZhiDaoApiException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ZhiDaoApiException(Throwable throwable) {
        super(throwable);
    }
    
}
