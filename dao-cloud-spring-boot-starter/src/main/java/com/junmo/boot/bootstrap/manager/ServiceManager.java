package com.junmo.boot.bootstrap.manager;

import com.junmo.boot.bootstrap.unit.ServiceInvoker;
import com.junmo.core.model.ProviderModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sucf
 * @date: 2023/2/16 19:37
 * @description:
 */
public class ServiceManager {
    /**
     * local class objects
     * key: provider + version
     * value: object bean
     */
    private static final Map<ProviderModel, ServiceInvoker> serviceInvokers = new HashMap<>();

    public static Map<ProviderModel, ServiceInvoker> getServiceInvokers() {
        return serviceInvokers;
    }

    public static void addService(String provider, int version, ServiceInvoker serviceInvoker) {
        ProviderModel providerModel = new ProviderModel();
        providerModel.setProvider(provider);
        providerModel.setVersion(version);
        serviceInvokers.put(providerModel, serviceInvoker);
    }

    public static ServiceInvoker getServiceInvoker(String provider, int version) {
        ProviderModel providerModel = new ProviderModel();
        providerModel.setProvider(provider);
        providerModel.setVersion(version);
        ServiceInvoker serviceInvoker = serviceInvokers.get(providerModel);
        return serviceInvoker;
    }

}
