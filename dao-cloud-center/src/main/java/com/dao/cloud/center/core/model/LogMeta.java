package com.dao.cloud.center.core.model;

import com.dao.cloud.core.model.LogModel;
import lombok.Data;

/**
 * @author: sucf
 * @date: 2024/9/8 22:18
 * @description:
 */
@Data
public class LogMeta implements Comparable {

    private String stage;
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
