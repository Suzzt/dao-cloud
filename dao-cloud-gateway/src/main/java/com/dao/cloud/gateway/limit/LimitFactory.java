package com.dao.cloud.gateway.limit;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.LimitModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: sucf
 * @date: 2024/3/5 22:22
 * @description: limiter factory
 */
@Slf4j
public class LimitFactory {
    public static Limiter getLimiter(LimitModel limitModel) {
        switch (limitModel.getLimitAlgorithm()) {
            case DaoCloudConstant.SLIDE_WINDOW_COUNT_ALGORITHM:
                return new SlideWindowCountLimiter(limitModel.getSlideWindowMaxRequestCount(), limitModel.getSlideDateWindowSize());
            case DaoCloudConstant.TOKEN_BUCKET_ALGORITHM:
                return new TokenBucketLimiter(limitModel.getTokenBucketMaxSize(), limitModel.getTokenBucketRefillRate());
            case DaoCloudConstant.LEAKY_BUCKET_ALGORITHM:
                // todo
                return null;
            default:
                log.error("Unable to handle unknown current limiting algorithm. limiter={}", limitModel);
                throw new DaoException("Unable to handle unknown current limiting algorithm.");
        }
    }
}
