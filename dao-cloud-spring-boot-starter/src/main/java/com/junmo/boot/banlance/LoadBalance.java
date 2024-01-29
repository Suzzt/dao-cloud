package com.junmo.boot.banlance;

import com.junmo.boot.banlance.impl.HashLoadBalance;
import com.junmo.boot.banlance.impl.RandomLoadBalance;
import com.junmo.boot.banlance.impl.RoundLoadBalance;

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




