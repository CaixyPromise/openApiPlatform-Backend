package com.caixy.backend.model.vo;

import com.caixy.backend.model.entity.InterfaceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 调用次数统计
 *
 * @name: com.caixy.project.model.vo.InterfaceInvokeCountVO
 * @author: CAIXYPROMISE
 * @since: 2023-12-21 15:56
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class InterfaceInvokeCountVO extends InterfaceInfo
{
    private int totalNum;
    private int successNum;
    private int failNum;
    private static final long serialVersionUID = 1L;
    public InterfaceInvokeCountVO()
    {}
}
