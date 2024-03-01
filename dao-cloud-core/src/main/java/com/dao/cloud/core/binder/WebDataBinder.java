package com.dao.cloud.core.binder;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author wuzhenhong
 * @date 2024/2/6 16:04
 */
public class WebDataBinder {

    public static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();

    // 默认自动扩充的集合为256，避免恶意传入index很大的参数
    private static final int DEFAULT_AUTO_GROW_COLLECTION_LIMIT = 256;

    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields = false;

    private boolean autoGrowNestedPaths = true;

    private int autoGrowCollectionLimit = DEFAULT_AUTO_GROW_COLLECTION_LIMIT;

    @Nullable
    private transient BeanWrapper beanWrapper;

    @Nullable
    private final Object target;
    private final String objectName;

    public WebDataBinder(@Nullable Object target, ConversionService conversionService) {
        if (Objects.isNull(target)) {
            throw new RuntimeException("要绑定的对象不能设置为空！");
        }
        this.target = target;
        this.objectName = target.getClass().getName();
        this.initBeanWrapper(conversionService);
    }

    private void initBeanWrapper(ConversionService conversionService) {
        this.beanWrapper = this.createBeanWrapper();
        this.beanWrapper.setExtractOldValueForEditor(true);
        this.beanWrapper.setAutoGrowNestedPaths(this.autoGrowNestedPaths);
        this.beanWrapper.setAutoGrowCollectionLimit(this.autoGrowCollectionLimit);
        if (conversionService != null) {
            this.beanWrapper.setConversionService(conversionService);
        }
    }

    public void bind(MutablePropertyValues mpvs) {
        this.adaptEmptyArrayIndices(mpvs);
        this.applyPropertyValues(mpvs);
    }

    protected void applyPropertyValues(MutablePropertyValues mpvs) {
        // Bind request parameters onto target object.
        this.beanWrapper.setPropertyValues(mpvs, this.ignoreUnknownFields, this.ignoreInvalidFields);
    }

    protected void adaptEmptyArrayIndices(MutablePropertyValues mpvs) {
        for (PropertyValue pv : mpvs.getPropertyValues()) {
            String name = pv.getName();
            if (name.endsWith("[]")) {
                String field = name.substring(0, name.length() - 2);
                if (this.beanWrapper.isWritableProperty(field) && !mpvs.contains(field)) {
                    mpvs.add(field, pv.getValue());
                }
                mpvs.removePropertyValue(pv);
            }
        }
    }

    protected BeanWrapper createBeanWrapper() {
        if (this.target == null) {
            throw new IllegalStateException("Cannot access properties on null bean instance '" + this.objectName + "'");
        }
        return PropertyAccessorFactory.forBeanPropertyAccess(this.target);
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        this.autoGrowNestedPaths = autoGrowNestedPaths;
    }

    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        this.autoGrowCollectionLimit = autoGrowCollectionLimit;
    }
}
