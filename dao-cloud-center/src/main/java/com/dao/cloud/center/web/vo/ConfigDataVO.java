package com.dao.cloud.center.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/7/30 15:58
 */
@Data
public class ConfigDataVO {
    private List<ConfigVO> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
}
