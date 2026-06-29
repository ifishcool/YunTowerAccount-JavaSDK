package com.yuntower.account.sdk;

import com.yuntower.account.sdk.api.AuthApi;
import com.yuntower.account.sdk.api.UserApi;
import com.yuntower.account.sdk.http.YuntowerHttpClient;

/**
 * 云塔账号 SDK 入口，线程安全，全局一个实例即可.
 *
 * <pre>{@code
 * // 简单用法
 * YuntowerAccountClient client = YuntowerAccountClient.create("appId", "appSecret");
 *
 * // 自定义配置
 * YuntowerAccountClient client = new YuntowerAccountClient(
 *     new YuntowerAccountConfig("appId", "appSecret")
 *         .connectTimeout(5000)
 *         .readTimeout(15000));
 *
 * // 登录
 * TokenResponse token = client.auth().getToken(code);
 * UserProfileResponse user = client.user().getUserInfo(token.getAccessToken());
 * }</pre>
 */
public class YuntowerAccountClient {

    private final AuthApi authApi;
    private final UserApi userApi;
    private final YuntowerTokenManager tokenManager;

    /** 快捷创建（使用默认 API 地址） */
    public static YuntowerAccountClient create(String appId, String appSecret) {
        return new YuntowerAccountClient(new YuntowerAccountConfig(appId, appSecret));
    }

    /** @param config 自定义配置（地址、超时等） */
    public YuntowerAccountClient(YuntowerAccountConfig config) {
        YuntowerHttpClient http = new YuntowerHttpClient(config);
        this.authApi = new AuthApi(http, config.getAppId(), config.getAppSecret());
        this.userApi = new UserApi(http, config.getAppId(), config.getAppSecret());
        this.tokenManager = new YuntowerTokenManager(authApi);
    }

    /** 认证接口：获取 Token、刷新 Token、退出登录 */
    public AuthApi auth() { return authApi; }

    /** 用户接口：获取用户信息、修改昵称/头像、查询第三方账号绑定 */
    public UserApi user() { return userApi; }

    /** Token 管理器：自动检测过期并刷新 */
    public YuntowerTokenManager tokenManager() { return tokenManager; }
}
