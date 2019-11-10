package com.qingcheng.entity;

import org.junit.runner.RunWith;

import java.io.Serializable;

/**
 * 返回前端的消息封装
 */
public class Result implements Serializable {

    /**
     * 状态码：0，执行成功；1，执行失败
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;//信息

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 默认执行成功
     */
    public Result() {
        this.code=0;
        this.message = "执行成功";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
