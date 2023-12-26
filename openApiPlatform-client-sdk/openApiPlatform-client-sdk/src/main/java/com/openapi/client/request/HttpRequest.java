package com.openapi.client.request;

import cn.hutool.crypto.SignUtil;
import cn.hutool.http.HttpResponse;
import com.caixy.openApiPlatformEncryptionAlgorithm.SignUtils;
import com.openapi.client.constants.UrlConstants;

import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Name: com.openapi.client.request.HttpRequest
 * @Description: 用于发送网络request的方法类
 * @Author: CAIXYPROMISE
 * @Date: 2023-12-19 15:00
 **/
public class HttpRequest
{
    private final String accessKey;
    private final String secretKey;

    @Getter
    private HttpResponse httpResponse = null;

    public HttpRequest(String accessKey, String secretKey)
    {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    /**
     * 生成请求头
     *
     * @param body 请求体内容
     */
    private Map<String, String> makeHeader(Object body) throws UnsupportedEncodingException
    {
        HashMap<String, String> header = new HashMap<>();
        Long timestamp = System.currentTimeMillis() / 1000;
        header.put(UrlConstants.HEADER_KEY_CONTENT_TYPE, "application/json");
        header.put(UrlConstants.HEADER_KEY_ACCESS_KEY, accessKey);
        header.put(UrlConstants.HEADER_KEY_SECRET_KEY, SignUtils.encodeSecretKey(secretKey, body.toString(), timestamp));
        header.put(UrlConstants.HEADER_KEY_TIMESTAMP, timestamp.toString());
        header.put(UrlConstants.HEADER_KEY_BODY, URLEncoder.encode(body.toString(), StandardCharsets.UTF_8));
        return header;
    }

    /**
     * 使用hutool发起POST请求，传入body发起请求
     *
     * @param path 请求路径
     * @param body 请求体内容
     */
    public boolean requestUsingPost(String path, Object body)
            throws UnsupportedEncodingException
    {
        Map<String, String> header = makeHeader(body);
        httpResponse = cn.hutool.http.HttpRequest.post(UrlConstants.API_HOST + path)
                .addHeaders(header)
                .body(body.toString())
                .execute();
        return httpResponse.getStatus() == 200;
    }

    /**
     * 使用hutool发起GET请求，传入body发起请求
     *
     * @param path 请求路径
     * @param body 请求体内容
     */
    public boolean requestUsingGet(String path, Object body)
            throws UnsupportedEncodingException
    {
        Map<String, String> header = makeHeader(body);
        httpResponse = cn.hutool.http.HttpRequest.get(UrlConstants.API_HOST + path)
                .addHeaders(header)
                .body(body.toString())
                .execute();
        return httpResponse.getStatus() == 200;
    }
}
