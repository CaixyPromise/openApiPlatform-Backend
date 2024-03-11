package com.caixy.backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基于仅用id的请求体
 *
 * @name: com.caixy.backend.common.IdRequest
 * @author: CAIXYPROMISE
 * @since: 2024-03-11 02:17
 **/
@Data
public class IdRequest implements Serializable
{
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}
