package com.media.dongfeng.model;

import java.io.Serializable;

public class ErrorMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 118006757510500485L;
    /**
     * 错误码
     */
    public String errno;
    /**
     * 错误信息
     */
    public String errmsg;
	
    /**
     * 错误的url
     */
    public String errurl;
    
    public ErrorMessage(String json) {
        
    }
}