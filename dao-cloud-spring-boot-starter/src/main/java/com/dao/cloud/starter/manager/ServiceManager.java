package com.dao.cloud.starter.manager;

import com.dao.cloud.starter.unit.ServiceInvoker;
import com.dao.cloud.core.model.ProviderModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sucf
 * @since 1.0.0
 * @date 2023/2/16 19:37
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

    /**
     * add service invoker(Non-thread-safe)
     *
     * @param provider
     * @param version
     * @param serviceInvoker
     */
    public static void addService(String provider, int version, ServiceInvoker serviceInvoker) {
        ProviderModel providerModel = new ProviderModel();
        providerModel.setProvider(provider);
        providerModel.setVersion(version);
        serviceInvokers.put(providerModel, serviceInvoker);
    }

    /**
     * get service all invoker
     *
     * @param provider
     * @param version
     * @return
     */
    public static ServiceInvoker getServiceInvoker(String provider, int version) {
        ProviderModel providerModel = new ProviderModel();
        providerModel.setProvider(provider);
        providerModel.setVersion(version);
        ServiceInvoker serviceInvoker = serviceInvokers.get(providerModel);
        return serviceInvoker;
    }
}
