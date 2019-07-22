package com.pinyougou.entity;

import java.io.Serializable;

/**
 * 操作信息回馈实体类
 */
public class Result implements Serializable {


    private Boolean success;//操作状态
    private String message;//操作信息

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
