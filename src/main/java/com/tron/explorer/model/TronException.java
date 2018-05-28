package com.tron.explorer.model;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


public class TronException extends Exception {
 
	private static final long serialVersionUID = 1L;
	private int statusCode = -1;
    private int errorCode = -1;
    private String request;
    private String error;

    public TronException(String msg) {
        super(msg);
    }

    public TronException(Exception cause) {
        super(cause);
    }
    
    public TronException(String msg , int statusCode) throws JSONException {
    	super(msg);
    	this.statusCode = statusCode;
    }

    public TronException(String msg , JSONObject json, int statusCode) throws JSONException {
        super(msg + "\n error:" + json.getString("error") +" error_code:" + json.getIntValue("error_code") + json.getString("request"));
        this.statusCode = statusCode;
        this.errorCode = json.getIntValue("error_code");
        this.error = json.getString("error");
        this.request = json.getString("request");

    }

    public TronException(String msg, Exception cause) {
        super(msg, cause);
    }

    public TronException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;

    }

    public int getStatusCode() {
        return this.statusCode;
    }

	public int getErrorCode() {
		return errorCode;
	}

	public String getRequest() {
		return request;
	}

	public String getError() {
		return error;
	}
    
}
