package com.dao.cloud.starter.banlance.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dao.cloud.core.enums.CodeEnum;
import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.starter.banlance.DaoLoadBalance;
import com.dao.cloud.starter.bootstrap.unit.Client;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author: sucf
 * @date: 2023/7/6 23:59
 * @description: Hash Load Balance
 */
public class HashLoadBalance extends DaoLoadBalance {
    @Override
    public Client route(Set<Client> availableClients) {
        if(CollectionUtil.isEmpty(availableClients)) {
            throw new DaoException(CodeEnum.SERVICE_PROVIDER_NOT_EXIST.getCode());
        }
        TreeMap<Long, Client> virtualClients = new TreeMap<>();
        int groupCount = availableClients.size() / 4;
        groupCount = groupCount == 0 ? 1 : groupCount;
        for(Client client : availableClients) {
            for (int i = 0; i < groupCount; i++) {
                byte[] digest = md5(client.getIp() + "hash参数" + i);
                for (int h = 0; h < 4; h++) {
                    long m = hash(digest, h);
                    // 创建虚拟节点
                    virtualClients.put(m, client);
                }
            }
        }
        // todo: 请传入hash参数，请作者好好改造
        long target = this.hash(this.md5(""), 0);
        return virtualClients.ceilingEntry(target).getValue();
    }

    private long hash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
            | ((long) (digest[2 + number * 4] & 0xFF) << 16)
            | ((long) (digest[1 + number * 4] & 0xFF) << 8)
            | (digest[0 + number * 4] & 0xFF))
            & 0xFFFFFFFFL;
    }

    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }
}
