package com.openapi.client.client;

import cn.hutool.http.HttpResponse;
import com.openapi.client.request.HttpRequest;
import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * 开放接口Api客户端系统
 * 传入accessKey和secretKey调用指定接口
 */
public class OpenApiClient
{
    private final String accessKey;
    private final String secretKey;
    @Getter
    private HttpResponse httpResponse = null;
    private HttpRequest httpRequest = null;


    public OpenApiClient(String accessKey, String secretKey)
    {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        httpRequest = new HttpRequest(accessKey, secretKey);
    }

    public String makeRequest(String url, String method, HashMap<String, Object> payload) throws UnsupportedEncodingException
    {
        try {
            switch (method.toUpperCase())
            {
            case "GET":
                return httpRequest.requestUsingGet(url, payload);
            case "POST":
                return httpRequest.requestUsingPost(url, payload);
            default:
                throw new RuntimeException("Unsupported request method: " + method);
            }
        } catch (Exception e)
        {
            throw new UnsupportedEncodingException("Request failed: " + e.getMessage());
        }
    }

}
