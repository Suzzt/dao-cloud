package com.dao.cloud.core.model;

import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/8/22 23:19
 * @description: log data model
 */
@Data
public class LogModel extends Model implements Comparable {

    private String traceId;
    /**
     * log stage
     * 1-1-2
     */
    private String stage;
    private ProxyProviderModel proxyProviderModel;
    /**
     * Processing node info
     * ip+port
     */
    private String node;

    @Override
    public int compareTo(Object o) {
        LogModel s1 = (LogModel) o;
        String[] parts1 = s1.getStage().split("-");
        String[] parts2 = this.stage.split("-");

        int minLength = Math.min(parts1.length, parts2.length);

        for (int i = 0; i < minLength; i++) {
            int num1 = Integer.parseInt(parts1[i]);
            int num2 = Integer.parseInt(parts2[i]);

            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return Integer.compare(parts1.length, parts2.length);
    }
}
