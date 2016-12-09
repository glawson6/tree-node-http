package com.ttis.treenode.dto;

/**
 * Created by tap on 12/8/16.
 */
public class SimpleResponse  {

    private boolean success;
    private String msg;

    public SimpleResponse() {
    }

    /**
     * Convenient constructor.
     *
     * @param success success flag.
     */
    public SimpleResponse(boolean success) {
        this.success = success;
    }

    public SimpleResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

