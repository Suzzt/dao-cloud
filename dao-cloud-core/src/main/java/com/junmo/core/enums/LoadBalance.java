package com.junmo.core.enums;

import com.junmo.core.banlance.DaoLoadBalance;
import com.junmo.core.banlance.strategy.RandomLoadBalanceStrategy;
import com.junmo.core.banlance.strategy.RoundBalanceRoundStrategy;

/**
 * @author: sucf
 * @date: 2023/1/11 09:08
 * @description: load balance strategy
 */
public enum LoadBalance {

    RANDOM(new RandomLoadBalanceStrategy()),
    ROUND(new RoundBalanceRoundStrategy());


    public final DaoLoadBalance daoLoadBalance;

    LoadBalance(DaoLoadBalance daoLoadBalance) {
        this.daoLoadBalance = daoLoadBalance;
    }

    /**
     * get load balance strategy handler
     *
     * @param name
     * @return
     */
    public static DaoLoadBalance match(String name) {
        for (LoadBalance item : LoadBalance.values()) {
            if (item.name().equals(name)) {
                return item.daoLoadBalance;
            }
        }
        //default
        return RANDOM.daoLoadBalance;
    }
}




