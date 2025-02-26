package com.dao.cloud.starter.banlance;

import com.dao.cloud.starter.banlance.impl.HashLoadBalance;
import com.dao.cloud.starter.banlance.impl.RandomLoadBalance;
import com.dao.cloud.starter.banlance.impl.RoundLoadBalance;

/**
 * @author: sucf
 * @date: 2023/1/11 09:08
 * @description: load balance strategy
 */
public enum LoadBalance {

    /**
     * random
     */
    RANDOM(new RandomLoadBalance()),

    /**
     * hash
     */
    HASH(new HashLoadBalance()),

    /**
     * round
     */
    ROUND(new RoundLoadBalance());

    public DaoLoadBalance daoLoadBalance;

    LoadBalance(DaoLoadBalance daoLoadBalance) {
        this.daoLoadBalance = daoLoadBalance;
    }

    public DaoLoadBalance getDaoLoadBalance() {
        return daoLoadBalance;
    }

}




