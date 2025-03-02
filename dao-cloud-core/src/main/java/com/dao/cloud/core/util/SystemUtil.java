package com.dao.cloud.core.util;

import cn.hutool.system.oshi.OshiUtil;
import com.dao.cloud.core.model.PerformanceModel;
import com.sun.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import oshi.software.os.OSFileStore;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2022/12/29 22:06
 */
@Slf4j
public class SystemUtil {

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
        performanceModel.setIo(SystemUtil.getDiskIoPercentage());
        return performanceModel;
    }

    private static String getDiskIoPercentage() {

        List<OSFileStore> diskStores =
            Optional.ofNullable(OshiUtil.getOs().getFileSystem().getFileStores()).orElse(Collections.emptyList());
        long totalSpace = 0L;
        long usedSpace = 0L;
        for (OSFileStore disk : diskStores) {
            totalSpace += disk.getTotalSpace(); // 磁盘总大小，单位：字节
            usedSpace += disk.getUsableSpace(); // 磁盘可用空间，单位：字节
        }

        if(usedSpace != 0) {
            BigDecimal io = BigDecimal.valueOf(usedSpace).divide(BigDecimal.valueOf(totalSpace), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100L));
            return io.stripTrailingZeros().toPlainString() + "%";
        }
        return "0%";
    }
}
