package com.dao.cloud.example.rpc;

import com.dao.cloud.common.Demo2Service;
import com.dao.cloud.common.DemoService;
import com.dao.cloud.common.dto.ParamDTO;
import com.dao.cloud.core.ApiResult;
import com.dao.cloud.core.model.ProxyConfigModel;
import com.dao.cloud.starter.annotation.DaoReference;
import com.dao.cloud.starter.log.DaoCloudLogger;
import com.dao.cloud.starter.unit.ConfigCallBack;
import com.dao.cloud.starter.utils.DaoCloudConfig;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sucf
 * @since 1.0
 */
@RestController
@Slf4j
public class DemoController {

    @DaoReference(proxy = "demo")
    private DemoService demoService;

    @DaoReference(proxy = "demo")
    private Demo2Service demo2Service;

    /**
     * rpc调用测试
     *
     * @return
     */
    @RequestMapping("demo")
    public String demo() {
        long start = System.currentTimeMillis();
        String string1 = demoService.test("String1", 1, 1.1, 1L, true);
        long end = System.currentTimeMillis();
        return string1 + "====" + (end - start);
    }

    /**
     * rpc调用测试(重载)
     *
     * @return
     */
    @RequestMapping("demo_overload")
    public String demoOverLoad() {
        long start = System.currentTimeMillis();
        String string1 = demoService.test(new ParamDTO());
        long end = System.currentTimeMillis();
        return string1 + "====" + (end - start);
    }

    /**
     * rpc调用测试
     *
     * @return
     */
    @RequestMapping("demo2")
    public String demo2() {
        long start = System.currentTimeMillis();
        demo2Service.test();
        long end = System.currentTimeMillis();
        return "demo2" + "====" + (end - start);
    }

    /**
     * rpc调用测试(复杂聚合对象)
     *
     * @return
     */
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

    /**
     * 测试获取config信息(String)
     *
     * @return
     */
    @RequestMapping("test-config")
    public ApiResult config() {
        String conf = DaoCloudConfig.getConf("dao-cloud", "dao-cloud", String.class);
        return ApiResult.buildSuccess(conf);
    }

    /**
     * 测试获取config信息(组合)
     *
     * @return
     */
    @RequestMapping("test-config-complex")
    public ApiResult configComplex() {
        ConfigObject configObject = DaoCloudConfig.getConf("demo", "config-key", ConfigObject.class);
        return ApiResult.buildSuccess(configObject);
    }

    /**
     * 测试订阅config信息
     *
     * @return
     */
    @RequestMapping("test-subscribe-config")
    public ApiResult subscribe() {
        DaoCloudConfig.subscribe("demo", "config-key", new DemoConfigCallback());
        return ApiResult.buildSuccess("config subscribe success");
    }

    @RequestMapping("test-trace-log")
    public ApiResult traceLog() {
        log.info("1 print log data......");
        demoService.trace();
        String traceId = DaoCloudLogger.getTraceId();
        return ApiResult.buildSuccess(traceId);
    }

    @Data
    public static class ConfigObject {
        private String str;

        private Integer integer;

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ConfigObject{");
            sb.append("str='").append(str).append('\'');
            sb.append(", integer=").append(integer);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class DemoConfigCallback extends ConfigCallBack<ConfigObject> {
        @Override
        public void callback(ProxyConfigModel proxyConfigModel, ConfigObject configObject) {
            log.info("proxyConfigModel = {}, configObject = {} to do something......", proxyConfigModel, configObject);
        }
    }
}
