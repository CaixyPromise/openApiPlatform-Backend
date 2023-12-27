package com.openapi.client.client;

import cn.hutool.http.HttpResponse;
import com.openapi.client.request.HttpRequest;
import lombok.Getter;

import java.io.UnsupportedEncodingException;


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

    public String getUserNameByPost(String name) throws UnsupportedEncodingException
    {
        boolean ret = httpRequest.requestUsingPost("/api/name", name);
        if (!ret)
        {
            throw new UnsupportedEncodingException("请求失败");
        }
        httpResponse = httpRequest.getHttpResponse();
        return httpResponse.body();
    }

    public String getUserNameByGet(String name) throws UnsupportedEncodingException
    {
        boolean ret = httpRequest.requestUsingGet("/api/name?name", name);
        if (!ret)
        {
            throw new UnsupportedEncodingException("请求失败");
        }
        httpResponse = httpRequest.getHttpResponse();
        return httpResponse.body();
    }

    public Object makeRequest(String url, String method, Object body) throws UnsupportedEncodingException
    {
        try {
            switch (method.toUpperCase()) {
                case "GET":
                    System.out.println("url" + url);
                    httpRequest.requestUsingGet(url, body);
                    return httpResponse.body();

                case "POST":
                    httpRequest.requestUsingPost(url, body);
                    return httpResponse.body();

                default:
                    throw new RuntimeException("错误的请求方式");
            }
        }
        catch (Exception e)
        {
            throw new UnsupportedEncodingException("请求失败: " + e.getMessage());
        }
    }


}
