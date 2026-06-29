package com.yuntower.account.sdk;

import com.yuntower.account.sdk.api.AuthApi;
import com.yuntower.account.sdk.common.Messages;
import com.yuntower.account.sdk.model.response.TokenResponse;

/**
 * Token 自动刷新管理器.
 *
 * <pre>{@code
 * TokenResponse saved = loadFromDatabase(userId);
 * TokenResponse valid = client.tokenManager().getValidAccessToken(saved);
 * if (valid != saved) saveToDatabase(userId, valid);
 * }</pre>
 */
public class YuntowerTokenManager {

    private final AuthApi authApi;

    public YuntowerTokenManager(AuthApi authApi) {
        this.authApi = authApi;
    }

    /**
     * 获取有效的 access_token，距过期不足 5 分钟时自动用 refresh_token 刷新.
     *
     * @param currentToken 当前 token（从数据库/缓存中获取）
     * @return 有效 token（可能已刷新，注意回存数据库）
     */
    public TokenResponse getValidAccessToken(TokenResponse currentToken) {
        if (currentToken == null) throw new YuntowerAccountException(Messages.TOKEN_REQUIRED);
        if (currentToken.isAccessTokenExpiringSoon()) {
            return authApi.refreshToken(currentToken.getRefreshToken());
        }
        return currentToken;
    }
}
