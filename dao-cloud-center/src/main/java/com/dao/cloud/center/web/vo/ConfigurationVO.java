package com.dao.cloud.center.web.vo;

import com.dao.cloud.center.core.model.ConfigurationModel;
import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0
 */
@Data
public class ConfigurationVO {
    private List<ConfigurationModel> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
}
