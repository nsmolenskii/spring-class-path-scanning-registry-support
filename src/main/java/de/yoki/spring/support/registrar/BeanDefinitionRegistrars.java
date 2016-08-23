package de.yoki.spring.support.registrar;

import de.yoki.spring.support.factory.TypedFactoryBeanSupport;
import lombok.NoArgsConstructor;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BeanDefinitionRegistrars {

    private static final ScopeMetadataResolver SCOPE_METADATA_RESOLVER = new AnnotationScopeMetadataResolver();
    private static final BeanNameGenerator BEAN_NAME_GENERATOR = new AnnotationBeanNameGenerator();


    /**
     * Create configured {@link BeanDefinitionHolder} that use provided factory and add it into registry.
     */
    public static <E extends TypedFactoryBeanSupport<?>> void withTypedFactory(
            Class<E> factoryClass,
            AnnotatedBeanDefinition definition,
            BeanDefinitionRegistry registry
    ) {
        BeanDefinitionHolder holder = wrapDefinitionWithTypedFactory(factoryClass, definition, registry);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * @return configured {@link BeanDefinitionHolder}, that use provided factory.
     * @see AnnotatedBeanDefinitionReader#registerBean(Class)
     */
    static <E extends TypedFactoryBeanSupport<?>> BeanDefinitionHolder wrapDefinitionWithTypedFactory(
            Class<E> factoryClass,
            AnnotatedBeanDefinition definition,
            BeanDefinitionRegistry registry
    ) {
        AnnotationConfigUtils.processCommonDefinitionAnnotations(definition);

        String className = definition.getBeanClassName();
        String beanName = BEAN_NAME_GENERATOR.generateBeanName(definition, registry);
        ScopeMetadata scopeMetadata = SCOPE_METADATA_RESOLVER.resolveScopeMetadata(definition);

        definition.setBeanClassName(factoryClass.getName());
        definition.getPropertyValues().add("objectType", className);
        definition.setScope(scopeMetadata.getScopeName());


        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, beanName);
        return applyScopedProxyMode(scopeMetadata, holder, registry);
    }

    /**
     * @return wrapped {@link BeanDefinitionHolder} with applied scope proxy mode
     * @see AnnotationConfigUtils#applyScopedProxyMode(ScopeMetadata, BeanDefinitionHolder, BeanDefinitionRegistry)
     */
    static BeanDefinitionHolder applyScopedProxyMode(
            ScopeMetadata metadata,
            BeanDefinitionHolder definition,
            BeanDefinitionRegistry registry
    ) {

        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
    }
}
