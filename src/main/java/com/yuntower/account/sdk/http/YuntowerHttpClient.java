package com.yuntower.account.sdk.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuntower.account.sdk.YuntowerAccountConfig;
import com.yuntower.account.sdk.YuntowerAccountException;
import com.yuntower.account.sdk.common.Messages;
import com.yuntower.account.sdk.model.response.YuntowerApiResult;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** OkHttp 封装，统一 JSON 序列化和错误处理 */
public class YuntowerHttpClient {

    private static volatile OkHttpClient sharedHttpClient;
    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final YuntowerAccountConfig config;

    public YuntowerHttpClient(YuntowerAccountConfig config) {
        this.config = config;
        this.httpClient = getSharedClient();
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static OkHttpClient getSharedClient() {
        if (sharedHttpClient == null) {
            synchronized (YuntowerHttpClient.class) {
                if (sharedHttpClient == null) {
                    sharedHttpClient = new OkHttpClient.Builder()
                            .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                            .build();
                }
            }
        }
        return sharedHttpClient;
    }

    public <T> T post(String path, Map<String, Object> body, Class<T> dataType) {
        String url = config.getBaseUrl() + path;
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            Request request = new Request.Builder().url(url)
                    .post(RequestBody.create(jsonBody, JSON)).build();
            try (Response response = httpClient.newCall(request).execute()) {
                return handleObjectResponse(response, dataType);
            }
        } catch (YuntowerAccountException e) {
            throw e;
        } catch (IOException e) {
            throw new YuntowerAccountException(Messages.NETWORK_ERROR + e.getMessage());
        }
    }

    public void postVoid(String path, Map<String, Object> body) {
        post(path, body, Object.class);
    }

    public <T> List<T> postForList(String path, Map<String, Object> body, Class<T> elementType) {
        String url = config.getBaseUrl() + path;
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            Request request = new Request.Builder().url(url)
                    .post(RequestBody.create(jsonBody, JSON)).build();
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "{}";
                if (!response.isSuccessful()) throw buildError(response.code(), responseBody);

                JavaType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, elementType);
                JavaType resultType = objectMapper.getTypeFactory()
                        .constructParametricType(YuntowerApiResult.class, listType);
                YuntowerApiResult<List<T>> result = objectMapper.readValue(responseBody, resultType);
                if (result == null) throw new YuntowerAccountException(Messages.API_EMPTY_RESPONSE);
                if (!result.isSuccess()) throw buildError(response.code(), responseBody);
                return result.getData() != null ? result.getData() : List.of();
            }
        } catch (YuntowerAccountException e) {
            throw e;
        } catch (IOException e) {
            throw new YuntowerAccountException(Messages.NETWORK_ERROR + e.getMessage());
        }
    }

    public void uploadFile(String path, Map<String, Object> params, String accessToken,
                           byte[] fileBytes, String filename) {
        String url = config.getBaseUrl() + path;
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("access_token", accessToken);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
            builder.addFormDataPart("file", filename,
                    RequestBody.create(fileBytes, MediaType.parse("application/octet-stream")));
            Request request = new Request.Builder().url(url).post(builder.build()).build();
            try (Response response = httpClient.newCall(request).execute()) {
                handleObjectResponse(response, Object.class);
            }
        } catch (YuntowerAccountException e) {
            throw e;
        } catch (IOException e) {
            throw new YuntowerAccountException(Messages.UPLOAD_ERROR + e.getMessage());
        }
    }

    private <T> T handleObjectResponse(Response response, Class<T> dataType) throws IOException {
        String responseBody = response.body() != null ? response.body().string() : "{}";
        if (!response.isSuccessful()) throw buildError(response.code(), responseBody);

        YuntowerApiResult<T> result = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructParametricType(YuntowerApiResult.class, dataType));
        if (result == null) throw new YuntowerAccountException(Messages.API_EMPTY_RESPONSE);
        if (!result.isSuccess()) throw buildError(response.code(), responseBody);
        return result.getData();
    }

    private YuntowerAccountException buildError(int httpStatus, String responseBody) {
        try {
            YuntowerApiResult<Object> err = objectMapper.readValue(responseBody,
                    objectMapper.getTypeFactory().constructParametricType(YuntowerApiResult.class, Object.class));
            return new YuntowerAccountException(httpStatus, err.getCode(), err.getMessage(), responseBody);
        } catch (IOException e) {
            return new YuntowerAccountException("HTTP " + httpStatus + ": " + responseBody);
        }
    }
}
