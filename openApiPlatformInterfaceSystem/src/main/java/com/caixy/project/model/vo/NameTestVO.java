package com.caixy.project.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 名字测试返回类
 *
 * @name: com.caixy.project.model.vo.NameTestVO
 * @author: CAIXYPROMISE
 * @since: 2024-01-16 19:29
 **/

@Data
public class NameTestVO implements Serializable
{
    private String name;
    private Integer age;
    private static final long serialVersionUID = 1L;
}
