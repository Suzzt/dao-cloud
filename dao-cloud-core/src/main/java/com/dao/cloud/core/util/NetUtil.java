package com.dao.cloud.core.util;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author sucf
 * @since 1.0
 */
@Slf4j
public class NetUtil {
    private static final String ANY_HOST_VALUE = "0.0.0.0";
    private static final String LOCALHOST_VALUE = "127.0.0.1";
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}$");

    public static String getLocalIp() {
        InetAddress localAddress = getLocalAddress();
        return localAddress != null ? localAddress.getHostAddress() : null;
    }

    private static InetAddress getLocalAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            InetAddress validAddress = toValidAddress(localHost);
            if (validAddress != null) {
                return validAddress;
            }
        } catch (UnknownHostException e) {
            log.warn("无法获取本地主机信息", e);
        }

        return getFirstValidNetworkAddress();
    }

    private static InetAddress getFirstValidNetworkAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces == null) {
                log.warn("未找到网络接口");
                return null;
            }

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (isInvalidNetworkInterface(networkInterface)) {
                    continue;
                }

                InetAddress address = getFirstReachableAddress(networkInterface);
                if (address != null) {
                    return address;
                }
            }
        } catch (IOException e) {
            log.error("获取网络接口时发生IO异常", e);
        }

        log.warn("未找到有效的网络地址");
        return null;
    }

    private static boolean isInvalidNetworkInterface(NetworkInterface networkInterface) throws IOException {
        return networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp();
    }

    private static InetAddress getFirstReachableAddress(NetworkInterface networkInterface) {
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            InetAddress validAddress = toValidAddress(address);
            if (validAddress != null && isAddressReachable(validAddress)) {
                return validAddress;
            }
        }
        return null;
    }

    private static boolean isAddressReachable(InetAddress address) {
        try {
            return address.isReachable(100);
        } catch (IOException e) {
            log.debug("无法到达地址: {}", address, e);
            return false;
        }
    }

    private static InetAddress toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            return isPreferIPV6Address() ? normalizeV6Address((Inet6Address) address) : null;
        }
        return isValidV4Address(address) ? address : null;
    }

    private static boolean isPreferIPV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    private static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String hostAddress = address.getHostAddress();
        return hostAddress != null && IP_PATTERN.matcher(hostAddress).matches()
                && !ANY_HOST_VALUE.equals(hostAddress)
                && !LOCALHOST_VALUE.equals(hostAddress);
    }

    private static InetAddress normalizeV6Address(Inet6Address address) {
        String hostAddress = address.getHostAddress();
        int scopeIndex = hostAddress.lastIndexOf('%');
        if (scopeIndex > 0) {
            try {
                return InetAddress.getByName(hostAddress.substring(0, scopeIndex) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                log.debug("无法解析IPv6地址: {}", address, e);
            }
        }
        return address;
    }
}
