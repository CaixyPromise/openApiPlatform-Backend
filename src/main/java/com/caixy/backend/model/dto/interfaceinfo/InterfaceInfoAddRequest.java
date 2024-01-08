package com.caixy.backend.model.dto.interfaceinfo;

import com.caixy.backend.model.param.HeaderParam;
import com.caixy.backend.model.param.PayloadParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class InterfaceInfoAddRequest implements Serializable
{
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private List<PayloadParam> requestPayload;

    /**
     * 请求头
     */
    private List<HeaderParam> requestHeader;

    /**
     * 响应头
     */
    private List<HeaderParam> responseHeader;

    /**
     * 预期响应结果
     */
    private List<PayloadParam> responsePayload;


    /**
     * 请求类型
     */
    private String method;

    private static final long serialVersionUID = 1L;

}