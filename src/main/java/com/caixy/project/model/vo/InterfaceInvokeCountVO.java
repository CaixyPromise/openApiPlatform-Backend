package com.caixy.project.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 调用次数统计
 *
 * @name: com.caixy.project.model.vo.InterfaceInvokeCountVO
 * @author: CAIXYPROMISE
 * @since: 2023-12-21 15:56
 **/
@Data
@AllArgsConstructor
public class InterfaceInvokeCountVO implements Serializable
{
    private int totalNum;
    private int successNum;
    private int failNum;
    private static final long serialVersionUID = 1L;
}
