package com.yuntower.account.sdk.api;

import com.yuntower.account.sdk.common.Messages;
import com.yuntower.account.sdk.http.YuntowerHttpClient;
import com.yuntower.account.sdk.model.response.ConnectAccountResponse;
import com.yuntower.account.sdk.model.response.UserProfileResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApi {

    private static final long AVATAR_MAX_SIZE = 15 * 1024 * 1024;

    private final YuntowerHttpClient http;
    private final String appId;
    private final String appSecret;

    public UserApi(YuntowerHttpClient http, String appId, String appSecret) {
        this.http = http;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * 获取云塔账号的用户信息.
     *
     * @return uid、username、nickname、avatar、city、intro、email
     */
    @SuppressWarnings("unchecked")
    public UserProfileResponse getUserInfo(String accessToken) {
        Map<String, Object> body = baseParams();
        body.put("access_token", accessToken);

        Object raw = http.post("/user/data", body, Object.class);
        if (raw == null) return null;

        Map<String, Object> map;
        if (raw instanceof List<?> list && !list.isEmpty()) {
            map = (Map<String, Object>) list.get(0);
        } else if (raw instanceof Map) {
            map = (Map<String, Object>) raw;
        } else {
            return null;
        }

        UserProfileResponse profile = new UserProfileResponse();
        Map<String, Object> profileMap = (Map<String, Object>) map.get("profile");
        if (profileMap != null) {
            profile.setUid(profileMap.get("uid"));
            profile.setUsername((String) profileMap.get("username"));
            profile.setNickname((String) profileMap.get("nickname"));
            profile.setAvatar((String) profileMap.get("avatar"));
            profile.setCity((String) profileMap.get("city"));
            profile.setIntro((String) profileMap.get("intro"));
            profile.setEmail((String) map.get("email"));
        }
        return profile;
    }

    /**
     * 获取云塔账号绑定的第三方平台信息.
     *
     * @return 绑定列表，每个包含 platform 和 uid
     */
    public List<ConnectAccountResponse> getThirdPartyAccount(String accessToken) {
        Map<String, Object> body = baseParams();
        body.put("access_token", accessToken);
        return http.postForList("/user/connect", body, ConnectAccountResponse.class);
    }

    /** 修改云塔账号昵称（1-64 个字符） */
    public void updateNickname(String accessToken, String nickname) {
        if (nickname == null || nickname.isEmpty() || nickname.length() > 64) {
            throw new IllegalArgumentException(Messages.NICKNAME_LENGTH);
        }
        Map<String, Object> body = baseParams();
        body.put("access_token", accessToken);
        body.put("nickname", nickname);
        http.postVoid("/user/nickname", body);
    }

    /** 上传头像（文件 ≤15MB） */
    public void updateAvatar(String accessToken, File file) {
        if (file == null || !file.exists()) throw new IllegalArgumentException(Messages.AVATAR_NOT_EXIST);
        if (file.length() > AVATAR_MAX_SIZE) throw new IllegalArgumentException(Messages.AVATAR_EXCEED_SIZE);
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            http.uploadFile("/user/avatar", baseParams(), accessToken, bytes, file.getName());
        } catch (IOException e) {
            throw new RuntimeException(Messages.AVATAR_READ_ERROR, e);
        }
    }

    /** 上传头像（字节数组，≤15MB） */
    public void updateAvatar(String accessToken, byte[] imageBytes, String filename) {
        if (imageBytes == null || imageBytes.length == 0) throw new IllegalArgumentException(Messages.AVATAR_DATA_EMPTY);
        if (imageBytes.length > AVATAR_MAX_SIZE) throw new IllegalArgumentException(Messages.AVATAR_EXCEED_SIZE);
        http.uploadFile("/user/avatar", baseParams(), accessToken, imageBytes, filename);
    }

    private Map<String, Object> baseParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("appid", appId);
        params.put("appsecret", appSecret);
        return params;
    }
}
