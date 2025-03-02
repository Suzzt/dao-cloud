package com.dao.cloud.center.web.vo;

import com.dao.cloud.core.model.ConfigurationFileInformationModel;
import lombok.Data;

import java.util.List;

/**
 * @author sucf
 * @since 1.0.0
 */
@Data
public class ConfigurationVO {
    private List<ConfigurationFileInformationModel> data;
    private Integer recordsTotal;
    private Integer recordsFiltered;
}
