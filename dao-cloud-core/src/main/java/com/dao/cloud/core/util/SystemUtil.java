package com.dao.cloud.core.util;

import com.dao.cloud.core.model.PerformanceModel;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

/**
 * @author: sucf
 * @date: 2022/12/29 22:06
 * @description:
 */
@Slf4j
public class SystemUtil {

    private static final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

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

    public static PerformanceModel getSystemLoadStatus() {
        Runtime runtime = Runtime.getRuntime();

        // JVM已经从操作系统那里申请的总内存
        long totalMemory = runtime.totalMemory();
        // JVM中的空闲内存
        long freeMemory = runtime.freeMemory();
        // JVM已使用的内存
        long usedMemory = totalMemory - freeMemory;
        double memoryUsage = ((double) usedMemory / totalMemory) * 100;

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        // 获取当前JVM的CPU使用率。注意这个值的读取可能需要消耗一些时间，为了更准确的测量，可能需要多次读取然后取平均值。
        double processCpuLoad = osBean.getProcessCpuLoad() * 100;

        PerformanceModel performanceModel = new PerformanceModel();
        // 保留两位小数并加上百分号
        performanceModel.setMemory(String.format("%.2f%%", memoryUsage));
        performanceModel.setCpu(String.format("%.2f%%", processCpuLoad));
        // todo io

        return performanceModel;
    }
}
