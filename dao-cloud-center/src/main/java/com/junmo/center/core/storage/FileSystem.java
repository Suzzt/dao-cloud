package com.junmo.center.core.storage;


import cn.hutool.core.io.FileUtil;
import com.google.common.collect.Maps;
import com.junmo.center.bootstarp.DaoCloudConfigCenterProperties;
import com.junmo.core.expand.Persistence;
import com.junmo.core.model.ConfigModel;
import com.junmo.core.model.ProxyConfigModel;
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
 * @author: sucf
 * @date: 2023/2/27 23:55
 * @description: data in local file system data persistence
 * <p>
 * config data is written to the file system.
 * if there is no directory or file, just create it.
 * the following is the file address corresponding to the config data.
 * ｜  dir  ｜  dir  ｜    dir    ｜ file-name ｜
 * ｜ proxy ｜  key  ｜  version  ｜   value   ｜
 * </p>
 */
@Component
@ConditionalOnProperty(value = "dao-cloud.config.persistence", havingValue = "file-system")
public class FileSystem implements Persistence {

    private DaoCloudConfigCenterProperties.FileSystemSetting fileSystemSetting;

    @Autowired
    public FileSystem(DaoCloudConfigCenterProperties daoCloudConfigCenterProperties) {
        DaoCloudConfigCenterProperties.FileSystemSetting fileSystemSetting = daoCloudConfigCenterProperties.getFileSystemSetting();
        this.fileSystemSetting = fileSystemSetting;
        String pathPrefix = fileSystemSetting.getPathPrefix();
        if (!StringUtils.hasLength(pathPrefix)) {
            fileSystemSetting.setPathPrefix("/data/dao-cloud/config");
        }
    }

    @Override
    public void storage(ConfigModel configModel) {
        ProxyConfigModel proxyConfigModel = configModel.getProxyConfigModel();
        String path = makePath(proxyConfigModel);
        String configValue = configModel.getConfigValue();
        write(path, configValue);
    }

    @Override
    public void delete(ProxyConfigModel proxyConfigModel) {
        String path = makePath(proxyConfigModel);
        FileUtil.del(path);
    }

    @Override
    public Map<ProxyConfigModel, String> load() {
        Map<ProxyConfigModel, String> map = Maps.newConcurrentMap();
        String prefixPath = fileSystemSetting.getPathPrefix();
        List<String> proxyList = loopDirs(prefixPath);
        for (String proxy : proxyList) {
            List<String> keys = loopDirs(prefixPath + File.separator + proxy);
            for (String key : keys) {
                List<String> versions = FileUtil.listFileNames(prefixPath + File.separator + proxy + File.separator + key);
                for (String version : versions) {
                    String value = FileUtil.readUtf8String(prefixPath + File.separator + proxy + File.separator + key + File.separator + version);
                    ProxyConfigModel proxyConfigModel = new ProxyConfigModel(proxy, key, Integer.parseInt(version));
                    map.put(proxyConfigModel, value);
                }
            }
        }
        return map;
    }

    public String makePath(ProxyConfigModel proxyConfigModel) {
        String proxy = proxyConfigModel.getProxy();
        String key = proxyConfigModel.getKey();
        int version = proxyConfigModel.getVersion();
        String prefix = fileSystemSetting.getPathPrefix();
        return prefix + File.separator + proxy + File.separator + key + File.separator + version;
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
