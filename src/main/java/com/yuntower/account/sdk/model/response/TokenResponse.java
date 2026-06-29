package com.yuntower.account.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Token 响应，包含 access_token 和 refresh_token */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_token_expires_in")
    private long accessTokenExpiresIn;

    @JsonProperty("access_token_expires_at")
    private String accessTokenExpiresAt;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private long refreshTokenExpiresIn;

    @JsonProperty("refresh_token_expires_at")
    private String refreshTokenExpiresAt;

    /** 用户访问凭证，默认有效期 12 小时 */
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    /** access_token 剩余有效秒数 */
    public long getAccessTokenExpiresIn() { return accessTokenExpiresIn; }
    public void setAccessTokenExpiresIn(long accessTokenExpiresIn) { this.accessTokenExpiresIn = accessTokenExpiresIn; }

    /** access_token 到期时间 */
    public String getAccessTokenExpiresAt() { return accessTokenExpiresAt; }
    public void setAccessTokenExpiresAt(String accessTokenExpiresAt) { this.accessTokenExpiresAt = accessTokenExpiresAt; }

    /** 用户刷新凭证，默认有效期 24 天 */
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    /** refresh_token 剩余有效秒数 */
    public long getRefreshTokenExpiresIn() { return refreshTokenExpiresIn; }
    public void setRefreshTokenExpiresIn(long refreshTokenExpiresIn) { this.refreshTokenExpiresIn = refreshTokenExpiresIn; }

    /** refresh_token 到期时间 */
    public String getRefreshTokenExpiresAt() { return refreshTokenExpiresAt; }
    public void setRefreshTokenExpiresAt(String refreshTokenExpiresAt) { this.refreshTokenExpiresAt = refreshTokenExpiresAt; }

    /** access_token 距过期不足 5 分钟时返回 true */
    public boolean isAccessTokenExpiringSoon() { return accessTokenExpiresIn <= 300; }

    @Override
    public String toString() {
        return "TokenResponse{accessToken='" + mask(accessToken) +
               "', refreshToken='" + mask(refreshToken) +
               "', expiresIn=" + accessTokenExpiresIn + "}";
    }

    private static String mask(String s) {
        if (s == null || s.length() <= 12) return s;
        return s.substring(0, 10) + "...";
    }
}
