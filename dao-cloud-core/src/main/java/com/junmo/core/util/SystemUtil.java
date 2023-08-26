package com.junmo.core.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author: sucf
 * @date: 2022/12/29 22:06
 * @description:
 */
@Slf4j
public class SystemUtil {
    /**
     * get available port
     *
     * @return
     * @throws Exception
     */
    public static int getAvailablePort() throws Exception {
        return getAvailablePort(0);
    }

    /**
     * get available port
     *
     * @param defaultPort
     * @return
     */
    public static int getAvailablePort(int defaultPort) throws Exception {
        int portTmp = defaultPort;
        while (portTmp < 65535) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = defaultPort--;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new Exception("no available port.");
    }

    /**
     * check port used
     *
     * @param port
     * @return
     */
    public static boolean isPortUsed(int port) {
        boolean used;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            used = false;
        } catch (IOException e) {
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error(">>>>>>>>>>> socket close fair <<<<<<<<<<<", e);
                }
            }
        }
        return used;
    }
}
