package com.openapi.client.request;

import cn.hutool.core.util.RandomUtil;
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
        String nonce = RandomUtil.randomNumbers(5);
        header.put(UrlConstants.API_SOURCE_FROM, UrlConstants.API_SOURCE_VALUE);
        header.put(UrlConstants.HEADER_KEY_CONTENT_TYPE, UrlConstants.HEADER_KEY_CONTENT_VALUE);
        header.put(UrlConstants.HEADER_KEY_ACCESS_KEY, accessKey);
        header.put(UrlConstants.HEADER_KEY_SECRET_KEY, SignUtils.encodeSecretKey(secretKey, nonce, timestamp));
        header.put(UrlConstants.HEADER_KEY_TIMESTAMP, timestamp.toString());
        header.put(UrlConstants.HEADER_KEY_NONCE, nonce);
//        header.put(UrlConstants.HEADER_KEY_BODY, URLEncoder.encode(body.toString(), "utf-8"));
        return header;
    }

    /**
     * 使用hutool发起POST请求，传入body发起请求
     *
     * @param path 请求路径
     * @param body 请求体内容
     */

    public String requestUsingPost(String path, Object body)
            throws UnsupportedEncodingException
    {
        Map<String, String> header = makeHeader(body);
        httpResponse = cn.hutool.http.HttpRequest.post(UrlConstants.API_HOST + path)
                .addHeaders(header)
                .body(body.toString())
                .execute();
        return httpResponse.body();
    }

    /**
     * 使用hutool发起GET请求，传入body发起请求
     *
     * @param path 请求路径
     * @param body 请求体内容
     */
    public String requestUsingGet(String path, Map<String, Object> params, Object body)
            throws UnsupportedEncodingException
    {
        Map<String, String> header = makeHeader(body);
        String url = UrlConstants.API_HOST + path;

        // 添加参数到 URL
        if (params != null && !params.isEmpty())
        {
            url = cn.hutool.http.HttpUtil.urlWithForm(url, params, StandardCharsets.UTF_8, false);
        }

        httpResponse = cn.hutool.http.HttpRequest.get(url)
                .addHeaders(header)
                .execute();
        return httpResponse.body();
    }
}
