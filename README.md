# YunTower Account Java SDK

[![JDK](https://img.shields.io/badge/JDK-17%2B-blue)](https://adoptium.net/)

云塔账号通行证 Java SDK，基于 OAuth 2.0 标准协议。


## 环境要求

- JDK 17+
- Maven 3.6+

## 安装

### GitHub Packages

**1. 创建 Token**

GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic) →
Generate new token → 勾选 `read:packages`

**2. 配置 `~/.m2/settings.xml`**

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>你的GitHub用户名</username>
            <password>你的Token</password>
        </server>
    </servers>
</settings>
```

**3. pom.xml**

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/ifishcool/YunTowerAccount-JavaSDK</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.yuntower</groupId>
    <artifactId>yuntower-account-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 本地安装

```bash
git clone https://github.com/ifishcool/YunTowerAccount-JavaSDK.git
cd YunTowerAccount-JavaSDK && mvn clean install
```



## 快速开始

```java
// 创建客户端（全局一个实例，线程安全）
YuntowerAccountClient client = YuntowerAccountClient.create("appId", "appSecret");

// 1. 前端授权回调拿到 code → 换 token
TokenResponse token = client.auth().getToken(code);

// 2. 获取用户信息
UserProfileResponse user = client.user().getUserInfo(token.getAccessToken());

// 3. 用 uid 绑定你的系统用户
String uid      = user.getUid();
String nickname = user.getNickname();
String avatar   = user.getAvatar();

// 4. Token 过期前刷新
TokenResponse fresh = client.auth().refreshToken(token.getRefreshToken());

// 5. 退出
client.auth().logout(fresh.getAccessToken());
```

---

## API 参考

### client.auth()

| 方法 | 说明 |
|---|---|
| `getToken(code)` | code 换 access_token + refresh_token |
| `getToken(code, atkSec, rtkSec)` | 同上，自定义有效期 |
| `refreshToken(refreshToken)` | 刷新 token，旧的立即失效 |
| `logout(accessToken)` | 退出登录 |

```java
TokenResponse token = client.auth().getToken(code);
token.getAccessToken();             // 12 小时有效
token.getRefreshToken();            // 24 天有效
token.getAccessTokenExpiresIn();    // 剩余秒数
token.isAccessTokenExpiringSoon();  // 距过期 < 5 分钟
```

### client.user()

| 方法 | 说明 |
|---|---|
| `getUserInfo(accessToken)` | 获取用户信息 |
| `getThirdPartyAccount(accessToken)` | 第三方平台绑定列表 |
| `updateNickname(accessToken, nickname)` | 修改昵称（1-64 字符） |
| `updateAvatar(accessToken, file)` | 上传头像（≤ 15MB） |
| `updateAvatar(accessToken, bytes, filename)` | 上传头像（字节数组） |

```java
UserProfileResponse user = client.user().getUserInfo(accessToken);
user.getUid();       // 用户唯一 ID
user.getUsername();  // 用户名
user.getNickname();  // 昵称
user.getAvatar();    // 头像 URL
user.getCity();      // 城市
user.getIntro();     // 简介
user.getEmail();     // 邮箱

// 第三方账号绑定
List<ConnectAccountResponse> conns = client.user().getThirdPartyAccount(accessToken);
for (ConnectAccountResponse c : conns) {
    c.getPlatform();  // "codemao" / "pgaot" / "dao3"
    c.getUid();
}
```

### client.tokenManager()

```java
TokenResponse saved = loadFromDatabase(userId);
TokenResponse valid = client.tokenManager().getValidAccessToken(saved);
if (valid != saved) saveToDatabase(userId, valid);
```
