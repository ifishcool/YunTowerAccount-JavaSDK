package com.yuntower.account.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/** 云塔 API 统一响应包装，兼容 message 和 msg 两种字段名 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class YuntowerApiResult<T> {

    private int code;
    private String message;
    private T data;

    /** 业务状态码，0 或 200 表示成功 */
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }

    /** 响应消息 */
    public String getMessage() { return message; }

    @JsonSetter("message")
    public void setMessage(String message) { this.message = message; }

    @JsonSetter("msg")
    public void setMsg(String msg) { this.message = msg; }

    /** 响应数据 */
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    /** code 为 0 或 200 均视为成功 */
    public boolean isSuccess() { return code == 0 || code == 200; }
}
