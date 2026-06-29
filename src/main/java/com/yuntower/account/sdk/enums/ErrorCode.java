package com.yuntower.account.sdk.enums;

import java.util.HashMap;
import java.util.Map;

/** 云塔 API 错误码，对照官方文档 */
public enum ErrorCode {

    // ===== 应用 (301xxx) =====
    APP_NOT_EXIST(301001, "应用不存在"),
    APP_STATUS_ERROR(301002, "应用状态异常"),
    REDIRECT_URI_NOT_ALLOWED(301003, "未授权的回调地址"),
    APP_SECRET_ERROR(301004, "appsecret 错误"),
    APP_PARAM_MISSING(301005, "必要参数缺失"),

    // ===== 授权 (302xxx) =====
    TUID_EXPIRED(302001, "tuid 已过期"),
    TOKEN_ERROR(302002, "token 错误"),
    APPID_ERROR(302003, "appid 错误"),
    TUID_ERROR(302004, "tuid 错误"),
    AUTH_CANCELLED(302005, "授权已被用户取消"),
    PERMISSION_DENIED(302006, "未授权此权限"),

    // ===== 用户 (303xxx) =====
    USER_NOT_EXIST(303001, "用户不存在"),
    USER_STATUS_ERROR(303002, "用户状态异常"),

    // ===== 访问令牌 (4010xx) =====
    ACCESS_TOKEN_INVALID(401011, "身份验证令牌无效"),
    ACCESS_TOKEN_NOT_YET(401012, "令牌尚未生效"),
    ACCESS_TOKEN_EXPIRED(401013, "会话已过期"),
    ACCESS_TOKEN_UNKNOWN(401015, "令牌未知错误"),

    // ===== 刷新令牌 (4010xx) =====
    REFRESH_TOKEN_INVALID(401021, "刷新令牌无效"),
    REFRESH_TOKEN_NOT_YET(401022, "刷新令牌尚未生效"),
    REFRESH_TOKEN_EXPIRED(401023, "刷新会话已过期"),
    REFRESH_TOKEN_UNKNOWN(401025, "刷新令牌未知错误");

    private final int code;
    private final String message;
    private static final Map<Integer, ErrorCode> CODE_MAP = new HashMap<>();

    static {
        for (ErrorCode ec : values()) CODE_MAP.put(ec.code, ec);
    }

    ErrorCode(int code, String message) { this.code = code; this.message = message; }

    /** 错误码 */
    public int getCode() { return code; }

    /** 错误描述 */
    public String getMessage() { return message; }

    /** 根据 API 返回的 code 反查枚举，找不到返回 null */
    public static ErrorCode fromCode(int code) { return CODE_MAP.get(code); }
}
