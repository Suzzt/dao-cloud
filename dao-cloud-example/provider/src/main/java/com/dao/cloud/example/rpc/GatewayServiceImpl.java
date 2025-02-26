package com.dao.cloud.example.rpc;

import cn.hutool.json.JSONUtil;
import com.dao.cloud.starter.annotation.DaoService;
import com.dao.cloud.common.GatewayService;
import com.dao.cloud.common.dto.Param2DTO;
import com.dao.cloud.common.dto.ParamDTO;
import com.dao.cloud.core.model.DaoCloudServletResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;

/**
 * @author sucf
 * @since 1.0
 */
@DaoService(provider = "GatewayService", version = 0)
public class GatewayServiceImpl implements GatewayService {
    @Override
    public String test(String string1, int int1, double double1, long long1, boolean flag) {
        return null;
    }

    @Override
    public ParamDTO complex(ParamDTO paramDTO, Param2DTO param2DTO) {
        return null;
    }

    @Override
    public String gatewayTest1(ParamDTO paramDTO) {
        return JSONUtil.toJsonStr(paramDTO);
    }

    @Override
    public Param2DTO gatewayTest2(ParamDTO paramDTO) {
        Param2DTO param2DTO = new Param2DTO();
        param2DTO.setString2(paramDTO.getString());
        param2DTO.setByteValue2(paramDTO.getByteValue());
        param2DTO.setInteger2(paramDTO.getInteger());
        return param2DTO;
    }

    @Override
    public void gatewayTest3(DaoCloudServletResponse response) {
        response.addHeader("Content-Disposition", "attachment;filename=xx.txt");
        response.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), "application/vnd.ms-excel");
        response.setBodyData("hello world!".getBytes(StandardCharsets.UTF_8));
    }
}
