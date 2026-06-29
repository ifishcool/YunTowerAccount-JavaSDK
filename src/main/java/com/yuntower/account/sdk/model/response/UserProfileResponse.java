package com.yuntower.account.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 用户信息响应 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileResponse {

    private String uid;
    private String username;
    private String nickname;
    private String avatar;
    private String city;
    private String intro;
    private String email;

    /** 用户唯一 ID */
    public String getUid() { return uid; }
    /** 兼容 API 返回数字或字符串 */
    public void setUid(Object uid) { this.uid = uid != null ? uid.toString() : null; }

    /** 用户名 */
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /** 昵称 */
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    /** 头像 URL */
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    /** 城市 */
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    /** 个人简介 */
    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    /** 邮箱 */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "UserProfileResponse{uid=" + uid + ", nickname='" + nickname + "', username='" + username + "'}";
    }
}
