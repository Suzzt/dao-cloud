package com.dao.cloud.center.core.storage;


import cn.hutool.core.io.FileUtil;
import com.dao.cloud.center.core.model.ConfigurationProperty;
import com.dao.cloud.center.core.model.ServerProxyProviderNode;
import com.dao.cloud.center.properties.DaoCloudConfigCenterProperties;
import com.dao.cloud.center.web.vo.CallTrendVO;
import com.dao.cloud.core.model.*;
import com.dao.cloud.core.util.DaoCloudConstant;
import com.dao.cloud.core.util.GsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/27 23:55
 * data in local file system data persistence
 * <p>
 * config data is written to the file system.
 * if there is no directory or file, just create it.
 * the following is the file address corresponding to the config data.
 * ｜  dir  ｜  dir  ｜    dir    ｜ file-content ｜
 * ｜ proxy ｜  key  ｜  version  ｜    value     ｜
 * </p>
 *
 * <p>
 * Gateway configuration data is written to the file system.
 * If the directory or file does not exist, it is created.
 * The following is the file address corresponding to the gateway data.
 * ｜  dir  ｜     dir    ｜    dir    ｜   file-content   ｜
 * ｜ proxy ｜  provider  ｜  version  ｜    data (json)   ｜
 * </p>
 *
 * <p>
 * Service management configuration data is written to the file system.
 * If the directory or file does not exist, it is created.
 * The following is the file address corresponding to the service management data.
 * ｜  dir  ｜     dir    ｜    dir    ｜    dir    ｜   file-content   ｜
 * ｜ proxy ｜  provider  ｜  version  ｜  ip:port  ｜   data(status)   ｜
 * </p>
 *
 * <p>
 * The call trend data is written to the file system.
 * If the directory or file does not exist, it is created.
 * The following is the address of the file corresponding to the call trend data.
 * ｜  dir  ｜     dir    ｜    dir    ｜    dir       ｜ file-content ｜
 * ｜ proxy ｜  provider  ｜  version  ｜  method-name ｜     count    ｜
 * </p>
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "dao-cloud.center.storage.way", havingValue = "file-system")
public class FileSystem implements Persistence {

    /**
     * Config Storage Path
     */
    private final String configStoragePath;

    /**
     * Configuration Storage Path
     */
    private final String configurationStoragePath;

    /**
     * Gateway Storage Path
     */
    private final String gatewayStoragePath;

    /**
     * Server Storage Path
     */
    private final String serverStoragePath;

    /**
     * Method call Storage Path
     */
    private final String trendStoragePath;

    @Autowired
    public FileSystem(DaoCloudConfigCenterProperties daoCloudConfigCenterProperties) {
        DaoCloudConfigCenterProperties.FileSystemSetting fileSystemSetting = daoCloudConfigCenterProperties.getFileSystemSetting();
        String pathPrefix = fileSystemSetting.getPathPrefix();
        // default need to be set
        pathPrefix = StringUtils.hasLength(pathPrefix) ? pathPrefix : "/data/dao-cloud/data_storage";
        this.configStoragePath = pathPrefix + File.separator + DaoCloudConstant.CONFIG;
        this.configurationStoragePath = pathPrefix + File.separator + DaoCloudConstant.CONFIGURATION;
        this.gatewayStoragePath = pathPrefix + File.separator + DaoCloudConstant.GATEWAY;
        this.serverStoragePath = pathPrefix + File.separator + DaoCloudConstant.SERVER;
        this.trendStoragePath = pathPrefix + File.separator + DaoCloudConstant.CALL;
    }

    @Override
    public void storage(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String path = makePath(configStoragePath, proxy, key, String.valueOf(version));
        String configValue = configModel.getConfigValue();
        write(path, configValue);
    }

    @Override
    public void delete(ProxyConfigModel proxyConfigModel) {
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String path = makePath(configStoragePath, proxy, key, String.valueOf(version));
        FileUtil.del(path);
        // file gc
        fileGC(makePath(configStoragePath, proxy, key));
        fileGC(makePath(configStoragePath, proxy));
    }

    @Override
    public void storage(GatewayModel gatewayModel) {
        String proxy = gatewayModel.getProxyProviderModel().getProxy();
        String provider = gatewayModel.getProxyProviderModel().getProviderModel().getProvider();
        int version = gatewayModel.getProxyProviderModel().getProviderModel().getVersion();
        String path = makePath(gatewayStoragePath, proxy, provider, String.valueOf(version));
        String content = GsonUtils.toJson(gatewayModel.getGatewayConfigModel());
        write(path, content);
    }

    @Override
    public void delete(ProxyProviderModel proxyProviderModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String path = makePath(gatewayStoragePath, proxy, provider, String.valueOf(version));
        FileUtil.del(path);
        // file gc
        String directory = makePath(gatewayStoragePath, proxy, provider);
        fileGC(directory);
        fileGC(makePath(gatewayStoragePath, proxy));
        fileGC(makePath(gatewayStoragePath));
    }

    @Override
    public void delete(ConfigurationProperty configurationProperty) {
        String filePath = configurationStoragePath + File.separator + configurationProperty.getProxy() + File.separator + configurationProperty.getGroupId() + File.separator + configurationProperty.getFileName();
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            log.error("Configuration file does not exist or is not a file: {}", filePath);
            return;
        }

        try {
            boolean deleted = FileUtil.del(file);
            if (deleted) {
                log.info("Successfully deleted configuration file: {}", filePath);
            } else {
                log.error("Failed to delete configuration file: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error deleting configuration file: {}", filePath, e);
        }
    }

    @Override
    public void storage(ProxyProviderModel proxyProviderModel, ServerNodeModel serverNodeModel) {
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String path = makePath(serverStoragePath, proxy, provider, String.valueOf(version), serverNodeModel.getIp() + "&" + serverNodeModel.getPort());
        write(path, String.valueOf(serverNodeModel.isStatus()));
    }

    @Override
    public void storage(ConfigurationProperty configurationProperty) {
        FileUtil.writeUtf8String(configurationProperty.getProperty(), configurationStoragePath + File.separator + configurationProperty.getProxy() + File.separator + configurationProperty.getGroupId() + File.separator + configurationProperty.getFileName());
    }

    private void fileGC(String path) {
        String[] files = new File(path).list();
        if (files == null || files.length == 0) {
            FileUtil.del(path);
        }
    }

    @Override
    public Map<ProxyConfigModel, String> loadConfig() {
        Map<ProxyConfigModel, String> map = Maps.newConcurrentMap();
        String prefixPath = configStoragePath;
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> keys = loopDirs(prefixPath + File.separator + proxy);
            for (String key : keys) {
                List<String> versions = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + key);
                for (String version : versions) {
                    try {
                        String value = FileUtil.readUtf8String(prefixPath + File.separator + proxy + File.separator + key + File.separator + version);
                        ProxyConfigModel proxyConfigModel = new ProxyConfigModel(proxy, key, Integer.parseInt(version));
                        map.put(proxyConfigModel, value);
                    } catch (Exception e) {
                        log.warn("Failed to load config data (proxy={}, key={}, version={}) from file", proxy, key, version, e);
                    }
                }
            }
        }
        return map;
    }

    @Override
    public Map<ProxyProviderModel, GatewayConfigModel> loadGateway() {
        Map<ProxyProviderModel, GatewayConfigModel> map = Maps.newConcurrentMap();
        String prefixPath = gatewayStoragePath;
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> providers = loopDirs(prefixPath + File.separator + proxy);
            for (String provider : providers) {
                List<String> versions = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + provider);
                for (String version : versions) {
                    try {
                        String value = FileUtil.readUtf8String(prefixPath + File.separator + proxy + File.separator + provider + File.separator + version);
                        if (value == null) {
                            continue;
                        }
                        GatewayConfigModel gatewayConfigModel = GsonUtils.fromJson(value, GatewayConfigModel.class);
                        ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, provider, Integer.parseInt(version));
                        map.put(proxyProviderModel, gatewayConfigModel);
                    } catch (Exception e) {
                        log.warn("Failed to load gateway limit data (proxy={}, provider={}, version={}) from file", proxy, provider, version, e);
                    }
                }
            }
        }
        return map;
    }

    @Override
    public Map<ServerProxyProviderNode, Boolean> loadServer() {
        Map<ServerProxyProviderNode, Boolean> map = Maps.newConcurrentMap();
        String prefixPath = serverStoragePath;
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> providers = loopDirs(prefixPath + File.separator + proxy);
            for (String provider : providers) {
                List<String> versions = loopDirs(prefixPath + File.separator + proxy + File.separator + provider);
                for (String version : versions) {
                    List<String> servers = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + provider + File.separator + version);
                    for (String server : servers) {
                        try {
                            String content = FileUtil.readUtf8String(prefixPath + File.separator + proxy + File.separator + provider + File.separator + version + File.separator + server);
                            if (!StringUtils.hasLength(content)) {
                                continue;
                            }
                            ProviderModel providerModel = new ProviderModel(provider, Integer.parseInt(version));
                            ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                            String[] split = server.split("&");
                            String ip = split[0];
                            Integer port = Integer.valueOf(split[1]);
                            ServerProxyProviderNode serverProxyProviderNode = new ServerProxyProviderNode(proxyProviderModel, ip, port);
                            Boolean status = Boolean.valueOf(content);
                            map.put(serverProxyProviderNode, status);
                        } catch (Exception e) {
                            log.warn("Failed to load server limit data (proxy={}, provider={}, version={}, server={}) from file", proxy, provider, version, server, e);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public List<ConfigurationFileInformationModel> getConfiguration() {
        List<ConfigurationFileInformationModel> configurationModels = Lists.newArrayList();
        String prefixPath = configurationStoragePath;
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> groupIds = loopDirs(prefixPath + File.separator + proxy);
            for (String groupId : groupIds) {
                List<String> files = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + groupId);
                for (String file : files) {
                    try {
                        if (!DaoCloudConstant.MACOS_HIDE_FILE_NAME.equals(file)) {
                            ConfigurationFileInformationModel configurationFileInformationModel = new ConfigurationFileInformationModel(proxy, groupId, file);
                            configurationModels.add(configurationFileInformationModel);
                        }
                    } catch (Exception e) {
                        log.error("Failed to load configuration data (proxy={}, groupId={}, fileName={}) from file", proxy, groupId, file, e);
                    }
                }
            }
        }
        return configurationModels;
    }

    @Override
    public String getConfigurationProperty(String proxy, String groupId, String fileName) {
        String filePath = configurationStoragePath + File.separator + proxy + File.separator + groupId + File.separator + fileName;
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            log.warn("Configuration file does not exist: {}", filePath);
            return null;
        }

        try {
            return FileUtil.readUtf8String(file);
        } catch (Exception e) {
            log.error("Error reading configuration file: {}", filePath, e);
            return null;
        }
    }

    @Override
    public void clear() {
        FileUtil.clean(gatewayStoragePath);
        FileUtil.clean(configStoragePath);
        FileUtil.clean(serverStoragePath);
        FileUtil.clean(trendStoragePath);
        FileUtil.clean(configurationStoragePath);
    }

    @Override
    public void callTrendIncrement(CallTrendModel callTrendModel) {
        ProxyProviderModel proxyProviderModel = callTrendModel.getProxyProviderModel();
        String proxy = proxyProviderModel.getProxy();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String path = makePath(trendStoragePath, proxy, provider, String.valueOf(version), callTrendModel.getMethodName());
        Long count;
        if (FileUtil.exist(path)) {
            count = Long.parseLong(FileUtil.readUtf8String(path)) + callTrendModel.getCount();
        } else {
            count = callTrendModel.getCount();
        }
        write(path, String.valueOf(count));
    }

    @Override
    public List<CallTrendVO> getCallCount(ProxyProviderModel proxyProviderModel) {
        List<CallTrendVO> result = new ArrayList<>();
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String path = makePath(trendStoragePath, proxyProviderModel.getProxy(), provider, String.valueOf(version));
        if (FileUtil.exist(path)) {
            List<String> files = FileUtil.listFileNames(path);
            for (String file : files) {
                try {
                    if (!DaoCloudConstant.MACOS_HIDE_FILE_NAME.equals(file)) {
                        String count = FileUtil.readUtf8String(path + File.separator + file);
                        CallTrendVO callTrendVO = new CallTrendVO(file, Long.valueOf(count));
                        result.add(callTrendVO);
                    }
                } catch (Exception e) {
                    log.error("File = {} that cannot be parsed", file, e);
                }
            }
        }
        return result;
    }

    @Override
    public void callTrendClear(ProxyProviderModel proxyProviderModel, String methodName) {
        String provider = proxyProviderModel.getProviderModel().getProvider();
        int version = proxyProviderModel.getProviderModel().getVersion();
        String path;
        if (StringUtils.hasLength(methodName)) {
            path = makePath(trendStoragePath, proxyProviderModel.getProxy(), provider, String.valueOf(version), methodName);
        } else {
            path = makePath(trendStoragePath, proxyProviderModel.getProxy(), provider, String.valueOf(version));
        }
        FileUtil.del(path);
    }

    @Override
    public List<CallTrendModel> getCallTrends() {
        List<CallTrendModel> callTrendModels = Lists.newArrayList();
        String prefixPath = trendStoragePath;
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> providers = loopDirs(prefixPath + File.separator + proxy);
            for (String provider : providers) {
                List<String> versions = loopDirs(prefixPath + File.separator + proxy + File.separator + provider);
                for (String version : versions) {
                    List<String> methods = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + provider + File.separator + version);
                    for (String method : methods) {
                        try {
                            if (!DaoCloudConstant.MACOS_HIDE_FILE_NAME.equals(method)) {
                                String count = FileUtil.readUtf8String(prefixPath + File.separator + proxy + File.separator + provider + File.separator + version + File.separator + method);
                                ProviderModel providerModel = new ProviderModel(provider, Integer.parseInt(version));
                                ProxyProviderModel proxyProviderModel = new ProxyProviderModel(proxy, providerModel);
                                CallTrendModel callTrendModel = new CallTrendModel(proxyProviderModel, method, Long.valueOf(count));
                                callTrendModels.add(callTrendModel);
                            }
                        } catch (Exception e) {
                            log.warn("Failed to load call trend data (proxy={}, provider={}, version={}, method={}) from file", proxy, provider, version, method, e);
                        }
                    }
                }
            }
        }
        return callTrendModels;
    }

    public String makePath(String prefix, String... modules) {
        for (String directory : modules) {
            prefix = prefix + File.separator + directory;
        }
        return prefix;
    }

    public List<String> loopDirs(String path) {
        List<File> files = FileUtil.loopFiles(new File(path), 1, new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        List<String> dirs = new ArrayList<>(files.size());
        for (File file : files) {
            dirs.add(file.getName());
        }
        return dirs;
    }

    public void write(String path, String data) {
        FileUtil.writeUtf8String(data, path);
    }
}
