package com.junmo.boot.banlance;

import com.junmo.boot.banlance.impl.HashLoadBalanceImpl;
import com.junmo.boot.banlance.impl.RandomLoadBalanceImpl;
import com.junmo.boot.banlance.impl.RoundLoadBalanceRoundImpl;

/**
 * @author: sucf
 * @date: 2023/1/11 09:08
 * @description: load balance strategy
 */
public enum LoadBalance {

    RANDOM(new RandomLoadBalanceImpl()),
    HASH(new HashLoadBalanceImpl()),
    ROUND(new RoundLoadBalanceRoundImpl());


    public DaoLoadBalance daoLoadBalance;

    LoadBalance(DaoLoadBalance daoLoadBalance) {
        this.daoLoadBalance = daoLoadBalance;
    }

    public DaoLoadBalance getDaoLoadBalance() {
        return daoLoadBalance;
    }

}




