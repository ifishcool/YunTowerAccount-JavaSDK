package com.yuntower.account.sdk;

import com.yuntower.account.sdk.common.Assert;
import com.yuntower.account.sdk.common.Messages;

/**
 * SDK 配置.
 *
 * <pre>{@code
 * new YuntowerAccountConfig("appId", "appSecret");
 * new YuntowerAccountConfig("appId", "appSecret", "https://custom-api.example.com");
 * }</pre>
 */
public class YuntowerAccountConfig {

    private final String appId;
    private final String appSecret;
    private final String baseUrl;
    private int connectTimeout = 10_000;
    private int readTimeout = 30_000;

    /** @param appId     应用 ID（在云塔后台创建应用后获取） */
    /** @param appSecret 应用密钥（绝不出现在前端代码中） */
    public YuntowerAccountConfig(String appId, String appSecret) {
        this(appId, appSecret, "https://v1.api.account.yuntower.com");
    }

    /** @param baseUrl 自定义 API 地址（内网部署时使用） */
    public YuntowerAccountConfig(String appId, String appSecret, String baseUrl) {
        Assert.notBlank(appId, Messages.APPID_REQUIRED);
        Assert.notBlank(appSecret, Messages.APPSECRET_REQUIRED);
        Assert.notBlank(baseUrl, Messages.BASEURL_REQUIRED);
        this.appId = appId;
        this.appSecret = appSecret;
        this.baseUrl = baseUrl;
    }

    public String getAppId() { return appId; }
    public String getAppSecret() { return appSecret; }
    public String getBaseUrl() { return baseUrl; }
    public int getConnectTimeout() { return connectTimeout; }
    public int getReadTimeout() { return readTimeout; }

    public YuntowerAccountConfig connectTimeout(int ms) { this.connectTimeout = ms; return this; }
    public YuntowerAccountConfig readTimeout(int ms) { this.readTimeout = ms; return this; }
}
