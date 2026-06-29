package com.yuntower.account.sdk.enums;

/** 授权权限范围，调用方通过 Web SDK 发起授权时指定 */
public enum Scope {

    /** 获取 UID、昵称、用户名、头像 */
    USER_PROFILE("user:profile"),

    /** 获取邮箱 */
    USER_EMAIL("user:email"),

    /** 获取编程猫账号 UID */
    CONNECT_CODEMAO_UID("connect:codemao_uid"),

    /** 获取 PGAot 账号 UID */
    CONNECT_PGAOT_UID("connect:pgaot_uid"),

    /** 获取神奇代码岛账号 UID */
    CONNECT_DAO3_UID("connect:dao3_uid");

    private final String value;

    Scope(String value) { this.value = value; }

    /** scope 字符串值，如 "user:profile" */
    public String getValue() { return value; }
}
