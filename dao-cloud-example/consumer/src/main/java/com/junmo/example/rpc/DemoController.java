package com.junmo.example.rpc;

import com.google.gson.Gson;
import com.junmo.boot.annotation.DaoReference;
import com.junmo.common.DemoService;
import com.junmo.common.dto.ParamDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: sucf
 * @date: 2023/1/12 17:38
 * @description:
 */
@RestController
public class DemoController {
    @DaoReference(proxy = "demo")
    private DemoService demoService;

    @RequestMapping("demo")
    public String demo() {
        long start = System.currentTimeMillis();
        String string1 = demoService.test("String1", 1, 1.1, 1L, true);
        long end = System.currentTimeMillis();
        return string1 + "====" + (end - start);
    }

    @RequestMapping("complex-demo")
    public String complex() {
        long start = System.currentTimeMillis();
        ParamDTO paramDTO = new ParamDTO();
        paramDTO.setString("string");
        paramDTO.setCharValue('c');
        paramDTO.setBooleanValue(true);
        paramDTO.setByteValue((byte) 1);
        paramDTO.setIntValue(1);
        paramDTO.setLongValue(1L);
        paramDTO = demoService.complex(paramDTO);
        long end = System.currentTimeMillis();
        return new Gson().toJson(paramDTO) + "====" + (end - start);
    }

    /**
     * 此方法就是为了测试rpc接口超时
     *
     * @return
     */
    @RequestMapping("time-out")
    public String timeOut() {
        demoService.timeout();
        return null;
    }
}
