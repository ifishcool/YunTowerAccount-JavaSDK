package com.yuntower.account.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 第三方平台账号绑定信息 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectAccountResponse {

    private String platform;
    private String uid;

    /** 平台类型：codemao / pgaot / dao3 */
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    /** 第三方平台 UID */
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}
