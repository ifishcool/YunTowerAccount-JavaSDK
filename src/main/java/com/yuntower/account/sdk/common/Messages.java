package com.yuntower.account.sdk.common;

/** SDK 内部错误消息常量 */
public final class Messages {

    private Messages() {}

    public static final String APPID_REQUIRED = "appId 不能为空";
    public static final String APPSECRET_REQUIRED = "appSecret 不能为空";
    public static final String BASEURL_REQUIRED = "baseUrl 不能为空";
    public static final String TOKEN_REQUIRED = "Token 不能为空";
    public static final String NETWORK_ERROR = "网络请求失败: ";
    public static final String UPLOAD_ERROR = "文件上传失败: ";
    public static final String API_EMPTY_RESPONSE = "API 返回空响应";
    public static final String NICKNAME_LENGTH = "昵称长度应在 1-64 个字符之间";
    public static final String AVATAR_NOT_EXIST = "头像文件不存在";
    public static final String AVATAR_EXCEED_SIZE = "头像文件不能超过 15MB";
    public static final String AVATAR_DATA_EMPTY = "头像数据不能为空";
    public static final String AVATAR_READ_ERROR = "读取头像文件失败";
    public static final String ACCESS_TOKEN_EXPIRE_LIMIT = "access_token 有效期不能超过 12 天";
    public static final String REFRESH_TOKEN_EXPIRE_LIMIT = "refresh_token 有效期不能超过 24 天";
}
