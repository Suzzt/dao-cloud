package com.dao.cloud.gateway.limit;

import com.dao.cloud.core.exception.DaoException;
import com.dao.cloud.core.model.LimitModel;
import com.dao.cloud.core.util.DaoCloudConstant;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sucf
 * @date 2024/3/5 22:22
 * limiter factory
 * @since 1.0.0
 */
@Slf4j
public class LimitFactory {

    public static final int SLIDE_WINDOW_COUNT_ALGORITHM = 1;
    public static final int TOKEN_BUCKET_ALGORITHM = 2;
    public static final int LEAKY_BUCKET_ALGORITHM = 3;

    public static Limiter getLimiter(LimitModel limitModel) {
        switch (limitModel.getLimitAlgorithm()) {
            case SLIDE_WINDOW_COUNT_ALGORITHM:
                return new SlideWindowCountLimiter(limitModel.getSlideWindowMaxRequestCount(), limitModel.getSlideDateWindowSize());
            case TOKEN_BUCKET_ALGORITHM:
                return new TokenBucketLimiter(limitModel.getTokenBucketMaxSize(), limitModel.getTokenBucketRefillRate());
            case LEAKY_BUCKET_ALGORITHM:
                return new LeakyBucketLimiter(limitModel.getLeakyBucketCapacity(), limitModel.getLeakyBucketRefillRate());
            default:
                log.error("Unable to handle unknown current limiting algorithm. limiter={}", limitModel);
                throw new DaoException("Unable to handle unknown current limiting algorithm.");
        }
    }
}
