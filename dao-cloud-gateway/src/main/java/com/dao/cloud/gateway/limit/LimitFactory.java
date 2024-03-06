package com.dao.cloud.gateway.limit;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.LimitModel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/3/5 22:22
 * @description: limiter factory
 */
@Slf4j
public class LimitFactory {
    public static Limiter getLimiter(LimitModel limitModel) {
        if (limitModel.getLimitAlgorithm() == 1) {
            return new SlideWindowCountLimiter(limitModel.getSlideWindowMaxRequestCount(), limitModel.getSlideDateWindowSize());
        } else {
            log.error("Unable to handle unknown current limiting algorithm. limiter={}", limitModel);
            throw new DaoException("Unable to handle unknown current limiting algorithm.");
        }
    }
}
