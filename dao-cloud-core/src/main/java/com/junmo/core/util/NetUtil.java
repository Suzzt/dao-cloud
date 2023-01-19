package com.junmo.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: sucf
 * @date: 2023/1/19 21:03
 * @description:
 */
public class NetUtil {
    public static String getServerIP(String domainName) {
        try {
            InetAddress inetAddress = InetAddress.getByName(domainName);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
        }
        return null;
    }
}
