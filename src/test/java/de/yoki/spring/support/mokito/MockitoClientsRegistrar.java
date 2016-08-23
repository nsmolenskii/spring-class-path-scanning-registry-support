package de.yoki.spring.support.mokito;

import de.yoki.spring.support.filter.Filter;
import de.yoki.spring.support.filter.Filters;
import de.yoki.spring.support.registrar.ClassPathScanningRegistrarSupport;
import de.yoki.spring.support.registrar.BeanDefinitionRegistrars;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class MockitoClientsRegistrar extends ClassPathScanningRegistrarSupport {

    @Override
    public void registerBeanDefinition(AnnotatedBeanDefinition definition, BeanDefinitionRegistry registry) {
        BeanDefinitionRegistrars.withTypedFactory(MockitoClientFactoryBean.class, definition, registry);
    }

    @Override
    public Filter includeFilter() {
        return Filters.annotated(MockitoClient.class);
    }

    @Override
    public Filter excludeFilter() {
        return Filters.nothing();
    }

}
