package com.yuntower.account.sdk;

import com.sun.net.httpserver.HttpServer;
import com.yuntower.account.sdk.model.response.ConnectAccountResponse;
import com.yuntower.account.sdk.model.response.TokenResponse;
import com.yuntower.account.sdk.model.response.UserProfileResponse;

import java.awt.Desktop;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 交互式登录测试 — 完整 OAuth 2.0 授权码流程.
 *
 * <p>使用: 设置环境变量 YUNTOWER_APP_ID、YUNTOWER_APP_SECRET，IDE 右键 Run main()
 *
 * <p>流程:
 * <ol>
 *   <li>生成随机 state（防CSRF），启动本地 HTTP 服务器 :9999</li>
 *   <li>打开浏览器 → 云塔授权页（redirect 模式）</li>
 *   <li>你点 "同意授权"</li>
 *   <li>云塔回调 localhost:9999/callback?from=YunTowerAccount&amp;status=success&amp;code=xxx&amp;state=xxx</li>
 *   <li>验证 state → SDK code 换 token → 拿用户信息 → 输出</li>
 * </ol>
 *
 * <p>注意：需在云塔后台将 {@code http://localhost:9999/callback} 加入应用白名单.
 */
public class InteractiveLoginTest {

    private static final int PORT = 9999;
    private static final String REDIRECT_PATH = "/callback";
    private static final String AUTH_URL = "https://account.yuntower.com";

    public static void main(String[] args) throws Exception {
        String appId = env("YUNTOWER_APP_ID");
        String appSecret = env("YUNTOWER_APP_SECRET");

        YuntowerAccountClient client = new YuntowerAccountClient(
                new YuntowerAccountConfig(appId, appSecret));

        // state 防 CSRF
        String state = UUID.randomUUID().toString();
        String redirectUri = "http://localhost:" + PORT + REDIRECT_PATH;

        // 等回调
        CountDownLatch latch = new CountDownLatch(1);
        String[] codeHolder = new String[1];
        String[] errorHolder = new String[1];

        // ===== 启动本地 HTTP 服务器 =====
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext(REDIRECT_PATH, exchange -> {
            String query = exchange.getRequestURI().getQuery();
            String from   = extractParam(query, "from");
            String status = extractParam(query, "status");
            String code   = extractParam(query, "code");
            String retState = extractParam(query, "state");

            // 验证来源和 state
            if (!"YunTowerAccount".equals(from) || !state.equals(retState)) {
                errorHolder[0] = "回调来源或 state 不匹配（可能被篡改）";
                latch.countDown(); return;
            }

            String html;
            if ("success".equals(status) && code != null) {
                codeHolder[0] = code;
                html = "<html><body style='text-align:center;padding-top:80px;font-family:sans-serif'>"
                     + "<h1>&#x2705; 授权成功</h1><p>窗口可以关闭了</p></body></html>";
            } else {
                errorHolder[0] = extractParam(query, "message");
                html = "<html><body style='text-align:center;padding-top:80px;font-family:sans-serif'>"
                     + "<h1>&#x274C; 授权失败</h1><p>"
                     + (errorHolder[0] != null ? errorHolder[0] : "用户取消") + "</p></body></html>";
            }

            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            latch.countDown();
        });
        server.start();

        // ===== 构建授权 URL（对齐 Web SDK） =====
        String scope = URLEncoder.encode("user:profile,user:email,connect:codemao_uid,connect:pgaot_uid,connect:dao3_uid", StandardCharsets.UTF_8);
        String fullRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        String authUrl = AUTH_URL + "/auth/app?type=redirect"
                + "&appid=" + appId
                + "&scope=" + scope
                + "&redirect_url=" + fullRedirect

                + "&state=" + state;

        System.out.println("============================================");
        System.out.println("  云塔 SDK 交互式登录测试");
        System.out.println("============================================");
        System.out.println("回调地址: " + redirectUri);
        System.out.println("state:    " + state);
        System.out.println();

        // ===== 打开浏览器 =====
        System.out.println("正在打开浏览器...");
        try {
            Desktop.getDesktop().browse(new URI(authUrl));
        } catch (Exception e) {
            System.out.println("未能自动打开浏览器，请手动打开:");
            System.out.println(authUrl);
        }

        System.out.println("等待授权... (5 分钟超时)");
        boolean ok = latch.await(5, TimeUnit.MINUTES);
        server.stop(0);

        if (!ok || codeHolder[0] == null) {
            System.err.println("\n❌ " + (errorHolder[0] != null ? errorHolder[0] : "超时或授权失败"));
            System.exit(1);
        }

        // ===== code → token → user info =====
        String code = codeHolder[0];
        System.out.println("\n拿到 code: " + mask(code));

        System.out.print("换取 token... ");
        TokenResponse token = client.auth().getToken(code);
        System.out.println("OK");
        System.out.println("  access_token:  " + mask(token.getAccessToken()));
        System.out.println("  refresh_token: " + mask(token.getRefreshToken()));
        System.out.println("  access 过期:   " + token.getAccessTokenExpiresIn() + "s"
                + (token.isAccessTokenExpiringSoon() ? " (即将过期)" : ""));
        System.out.println("  refresh 过期:  " + token.getRefreshTokenExpiresIn() + "s");

        System.out.print("获取用户信息... ");
        UserProfileResponse user = client.user().getUserInfo(token.getAccessToken());
        System.out.println("OK");

        System.out.println("\n============================================");
        System.out.println("  登录成功");
        System.out.println("============================================");
        System.out.printf("  UID:      %s%n", user.getUid());
        System.out.printf("  用户名:   %s%n", user.getUsername());
        System.out.printf("  昵称:     %s%n", user.getNickname());
        System.out.printf("  头像:     %s%n", user.getAvatar());
        if (user.getCity() != null)   System.out.printf("  城市:     %s%n", user.getCity());
        if (user.getIntro() != null)  System.out.printf("  简介:     %s%n", user.getIntro());
        if (user.getEmail() != null)  System.out.printf("  邮箱:     %s%n", user.getEmail());
        System.out.println("============================================");

        // 第三方账号绑定
        System.out.print("\n查询第三方账号绑定... ");
        try {
            java.util.List<ConnectAccountResponse> conns = client.user()
                    .getThirdPartyAccount(token.getAccessToken());
            System.out.println("OK (" + conns.size() + " 个绑定)");
            for (ConnectAccountResponse c : conns) {
                System.out.printf("  平台: %s | UID: %s%n", c.getPlatform(), c.getUid());
            }
        } catch (YuntowerAccountException e) {
            System.out.println("（未授权对应 scope 或无绑定）");
        }

        // 刷新 token
        System.out.print("\n刷新 Token... ");
        TokenResponse fresh = token;
        try {
            fresh = client.auth().refreshToken(token.getRefreshToken());
            System.out.println("OK");
            System.out.println("  新 access_token: " + mask(fresh.getAccessToken()));
        } catch (YuntowerAccountException e) {
            System.out.println("（刷新失败: " + e.getMessage() + "，用旧 token 继续）");
        }

        // 修改昵称（用最新有效 token）
        System.out.print("修改昵称... ");
        try {
            client.user().updateNickname(fresh.getAccessToken(), "Java SDK 测试");
            System.out.println("OK");
        } catch (YuntowerAccountException e) {
            System.out.println("（" + e.getMessage() + "）");
        }

        // 上传头像（跳过，需要真实文件）
        System.out.println("上传头像... 跳过（需本地文件）");

        System.out.print("\n退出登录... ");
        client.auth().logout(fresh.getAccessToken());
        System.out.println("完成 ✅");
    }

    private static String extractParam(String query, String key) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static String env(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) {
            System.err.println("❌ 请设置环境变量: export " + key + "=xxx");
            System.exit(1);
        }
        return v;
    }

    private static String mask(String s) {
        if (s == null || s.length() <= 12) return s;
        return s.substring(0, 10) + "...";
    }
}
