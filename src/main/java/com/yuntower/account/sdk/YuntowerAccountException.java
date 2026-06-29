package com.yuntower.account.sdk;

import com.yuntower.account.sdk.enums.ErrorCode;

/**
 * SDK 异常，包含 HTTP 状态码、业务 code、错误枚举、原始响应体.
 *
 * <p>用 {@link #format(Throwable)} 输出可读错误摘要.
 * <p>用 {@link #getErrorCode()} 匹配具体错误类型.
 */
public class YuntowerAccountException extends RuntimeException {

    private final int httpStatus;
    private final int code;
    private final ErrorCode errorCode;
    private final String responseBody;

    public YuntowerAccountException(int httpStatus, int code, String message, String responseBody) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.errorCode = ErrorCode.fromCode(code);
        this.responseBody = responseBody;
    }

    public YuntowerAccountException(int code, String message) {
        this(-1, code, message, null);
    }

    public YuntowerAccountException(ErrorCode errorCode) {
        this(-1, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public YuntowerAccountException(String message) {
        this(-1, -1, message, null);
    }

    /** HTTP 状态码（网络错误时为 -1） */
    public int getHttpStatus() { return httpStatus; }
    /** 云塔 API 业务 code */
    public int getCode() { return code; }
    /** 对应的错误码枚举（不在文档范围内时返回 null） */
    public ErrorCode getErrorCode() { return errorCode; }
    /** 接口返回的原始 body */
    public String getResponseBody() { return responseBody; }

    /**
     * 格式化异常为可读字符串，适合日志输出.
     * <pre>{@code log.error("{}", YuntowerAccountException.format(e)); }</pre>
     */
    public static String format(Throwable err) {
        if (err instanceof YuntowerAccountException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("[YunTowerAccount] ").append(e.getMessage());
            if (e.responseBody != null) sb.append("\nResponse: ").append(e.responseBody);
            if (e.httpStatus > 0) sb.append("\nHTTP ").append(e.httpStatus).append(" code=").append(e.code);
            return sb.toString();
        }
        return err.getMessage() != null ? err.getMessage() : err.toString();
    }

    @Override
    public String toString() {
        return "YuntowerAccountException{httpStatus=" + httpStatus + ", code=" + code +
               ", errorCode=" + (errorCode != null ? errorCode.name() : "null") +
               ", message='" + getMessage() + "'}";
    }
}
