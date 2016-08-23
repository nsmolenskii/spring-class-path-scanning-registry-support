package de.yoki.spring.support.factory;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Objects;

@Data
public abstract class TypedFactoryBeanSupport<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
    private Class<T> objectType;
    private ApplicationContext applicationContext;
    private final boolean isSingleton = true;

    protected abstract T createObject(Class<T> objectType);

    @Override
    public T getObject() throws Exception {
        return createObject(objectType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Objects.requireNonNull(objectType, "Object type is not defined");
        Objects.requireNonNull(applicationContext, "ApplicationContext is not defined");
    }

}