package com.junmo.center.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: sucf
 * @date: 2023/7/30 15:58
 * @description:
 */
@Data
public class ConfigDataVO {
    private List<ConfigVO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
}
