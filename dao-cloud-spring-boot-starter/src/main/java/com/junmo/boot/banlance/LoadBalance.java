package com.junmo.boot.banlance;

import com.junmo.boot.banlance.impl.RandomLoadBalanceImpl;
import com.junmo.boot.banlance.impl.RoundBalanceRoundImpl;

/**
 * @author: sucf
 * @date: 2023/1/11 09:08
 * @description: load balance strategy
 */
public enum LoadBalance {

    RANDOM(new RandomLoadBalanceImpl()),
    ROUND(new RoundBalanceRoundImpl());


    public DaoLoadBalance daoLoadBalance;

    LoadBalance(DaoLoadBalance daoLoadBalance) {
        this.daoLoadBalance = daoLoadBalance;
    }

    public DaoLoadBalance getDaoLoadBalance() {
        return daoLoadBalance;
    }

}




