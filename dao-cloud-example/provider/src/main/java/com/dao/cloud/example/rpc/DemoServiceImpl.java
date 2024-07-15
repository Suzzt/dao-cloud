package com.dao.cloud.example.rpc;

import com.dao.cloud.common.DemoService;
import com.dao.cloud.common.dto.ParamDTO;
import com.dao.cloud.starter.annotation.DaoCallTrend;
import com.dao.cloud.starter.annotation.DaoService;

import java.util.concurrent.TimeUnit;

/**
 * @author: sucf
 * @date: 2023/1/12 17:33
 * @description:
 */
@DaoService
public class DemoServiceImpl implements DemoService {
    @Override
    @DaoCallTrend(interval = 5, time_unit = TimeUnit.SECONDS)
    public String test(String string1, int int1, double double1, long long1, boolean flag) {
        return string1 + int1 + double1 + long1 + flag;
    }

    @Override
    public ParamDTO complex(ParamDTO paramDTO) {
        paramDTO.setCharValue('s');
        paramDTO.setString("string1");
        return paramDTO;
    }

    @Override
    public String timeout() {
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "time out";
    }
}
