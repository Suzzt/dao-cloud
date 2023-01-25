package com.junmo.common;

import com.junmo.common.dto.ParamDTO;

/**
 * @author: sucf
 * @date: 2023/1/12 17:33
 * @description:
 */
public interface DemoService {
    String test(String string1, int int1, double double1, long long1, boolean flag);

    ParamDTO complex(ParamDTO paramDTO);
}
