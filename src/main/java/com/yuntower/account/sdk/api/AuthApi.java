package com.yuntower.account.sdk.api;

import com.yuntower.account.sdk.common.Messages;
import com.yuntower.account.sdk.http.YuntowerHttpClient;
import com.yuntower.account.sdk.model.response.TokenResponse;

import java.util.HashMap;
import java.util.Map;

public class AuthApi {

    static final long ACCESS_TOKEN_MAX_EXPIRE = 12 * 24 * 3600;
    static final long REFRESH_TOKEN_MAX_EXPIRE = 24 * 24 * 3600;

    private final YuntowerHttpClient http;
    private final String appId;
    private final String appSecret;

    public AuthApi(YuntowerHttpClient http, String appId, String appSecret) {
        this.http = http;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * 用授权码换取 Token.
     *
     * @param code 前端 Web SDK 授权回调拿到的临时代码，有效期 10 分钟
     * @return access_token（12h）+ refresh_token（24d）
     */
    public TokenResponse getToken(String code) {
        return getToken(code, null, null);
    }

    /**
     * 用授权码换取 Token（可指定有效期）.
     *
     * @param accessTokenExpiresIn  access_token 有效期秒数，最大 12 天，null 用默认值
     * @param refreshTokenExpiresIn refresh_token 有效期秒数，最大 24 天，null 用默认值
     */
    public TokenResponse getToken(String code, Long accessTokenExpiresIn, Long refreshTokenExpiresIn) {
        if (accessTokenExpiresIn != null && accessTokenExpiresIn > ACCESS_TOKEN_MAX_EXPIRE) {
            throw new IllegalArgumentException(Messages.ACCESS_TOKEN_EXPIRE_LIMIT);
        }
        if (refreshTokenExpiresIn != null && refreshTokenExpiresIn > REFRESH_TOKEN_MAX_EXPIRE) {
            throw new IllegalArgumentException(Messages.REFRESH_TOKEN_EXPIRE_LIMIT);
        }
        Map<String, Object> body = baseParams();
        body.put("code", code);
        if (accessTokenExpiresIn != null && accessTokenExpiresIn > 0) {
            body.put("access_token_expires_in", accessTokenExpiresIn);
        }
        if (refreshTokenExpiresIn != null && refreshTokenExpiresIn > 0) {
            body.put("refresh_token_expires_in", refreshTokenExpiresIn);
        }
        return http.post("/user/token/get", body, TokenResponse.class);
    }

    /**
     * 用 refresh_token 刷新 Token.
     *
     * <p>调用后旧的 access_token 和 refresh_token 立即失效.
     */
    public TokenResponse refreshToken(String refreshToken) {
        Map<String, Object> body = baseParams();
        body.put("refresh_token", refreshToken);
        return http.post("/user/token/refresh", body, TokenResponse.class);
    }

    /** 退出登录，销毁 access_token */
    public void logout(String accessToken) {
        Map<String, Object> body = baseParams();
        body.put("access_token", accessToken);
        http.postVoid("/user/logout", body);
    }

    private Map<String, Object> baseParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("appsecret", appSecret);
        return params;
    }
}
