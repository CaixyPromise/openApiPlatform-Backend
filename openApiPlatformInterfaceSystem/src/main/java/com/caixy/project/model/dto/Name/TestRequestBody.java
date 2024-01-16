package com.caixy.project.model.dto.Name;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试Body
 *
 * @name: com.caixy.project.model.dto.Name.TestRequestBody
 * @author: CAIXYPROMISE
 * @since: 2024-01-16 19:26
 **/
@Data
public class TestRequestBody implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer age;
}
