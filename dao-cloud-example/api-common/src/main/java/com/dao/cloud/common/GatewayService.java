package com.dao.cloud.common;

import com.dao.cloud.common.dto.ParamDTO;
import com.dao.cloud.common.dto.Param2DTO;
import com.dao.cloud.core.model.DaoCloudServletResponse;

/**
 * @author sucf
 * @since 1.0
 */
public interface GatewayService {
    String test(String string1, int int1, double double1, long long1, boolean flag);

    ParamDTO complex(ParamDTO paramDTO, Param2DTO param2DTO);

    String gatewayTest1(ParamDTO paramDTO);

    Param2DTO gatewayTest2(ParamDTO paramDTO);

    void gatewayTest3(DaoCloudServletResponse response);
}
