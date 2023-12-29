package com.caixy.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInfoInvokeRequest implements Serializable
{

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求载荷: body/params
     */
    private String userRequestPayload;

    private static final long serialVersionUID = 1L;
}
