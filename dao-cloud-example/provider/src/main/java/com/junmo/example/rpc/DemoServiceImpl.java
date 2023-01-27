package com.junmo.example.rpc;

import com.junmo.boot.annotation.DaoService;
import com.junmo.common.DemoService;
import com.junmo.common.dto.ParamDTO;

/**
 * @author: sucf
 * @date: 2023/1/12 17:33
 * @description:
 */
@DaoService
public class DemoServiceImpl implements DemoService {
    @Override
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
