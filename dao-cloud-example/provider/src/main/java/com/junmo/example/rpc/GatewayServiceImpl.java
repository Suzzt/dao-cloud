package com.junmo.example.rpc;

import com.junmo.common.GatewayService;
import com.junmo.common.dto.Param2DTO;
import com.junmo.common.dto.ParamDTO;

/**
 * @author: sucf
 * @date: 2024/1/26 11:12
 * @description:
 */
public class GatewayServiceImpl implements GatewayService {
    @Override
    public String test(String string1, int int1, double double1, long long1, boolean flag) {
        return null;
    }

    @Override
    public ParamDTO complex(ParamDTO paramDTO, Param2DTO param2DTO) {
        return null;
    }
}
