package com.dao.cloud.common;

import com.dao.cloud.common.dto.ParamDTO;

/**
 * @author sucf
 * @since 1.0
 */
public interface DemoService {
    String test(String string1, int int1, double double1, long long1, boolean flag);

    String test(ParamDTO paramDTO);

    ParamDTO complex(ParamDTO paramDTO);

    String timeout();

    void trace();
}
