package de.yoki.spring.support.registrar;

import de.yoki.spring.support.filter.Filter;
import lombok.Data;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Base support class that register all class path for candidates.
 *
 * @apiNote Class should be imported from any meta-annotation with {@link Configuration} and {@link Import}.
 * @apiNote Packages to be scan could be overridden by {@link ComponentScan} and {@link AliasFor}.
 * @apiNote Candidates are filtered by {@link #includeFilter()} and {@link #excludeFilter()}.
 * @apiNote Every candidate should be registered from client code override within {@link #registerBeanDefinition}
 * <p>
 * See implementations and associated unit tests for usage examples.
 * @see AliasFor
 * @see ComponentScan
 * @see Configuration
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 */
@Data
public abstract class ClassPathScanningRegistrarSupport
        implements
        ImportBeanDefinitionRegistrar,
        EnvironmentAware,
        BeanFactoryAware,
        BeanClassLoaderAware,
        ResourceLoaderAware {

    private Environment environment;
    private BeanFactory beanFactory;
    private ClassLoader beanClassLoader;
    private ResourceLoader resourceLoader;

    protected abstract void registerBeanDefinition(AnnotatedBeanDefinition definition, BeanDefinitionRegistry registry);

    protected abstract Filter includeFilter();

    protected abstract Filter excludeFilter();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata registryMetadata, BeanDefinitionRegistry registry) {
        resolveBeanDefinitions(registryMetadata).forEach(definition -> registerBeanDefinition(definition, registry));
    }

    /**
     * @return resolved base packages to be scanned from base meta-annotation.
     * @see ConfigurationWarningsApplicationContextInitializer.ComponentScanPackageCheck#addComponentScanningPackages
     */
    protected Set<String> resolveBasePackages(AnnotationMetadata registryMetadata) {
        Map<String, Object> attributes = registryMetadata.getAnnotationAttributes(ComponentScan.class.getName(), true);
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(attributes);

        Set<String> basePackages = new HashSet<>();
        for (String pkg : annotationAttributes.getStringArray("basePackages")) {
            basePackages.add(pkg);
        }
        for (String clazz : annotationAttributes.getStringArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(registryMetadata.getClassName()));
        }
        return basePackages;
    }


    protected Stream<AnnotatedBeanDefinition> resolveBeanDefinitions(AnnotationMetadata metadata) {
        ClassPathScanningCandidateComponentProvider provider = createPackageComponentProvider();
        Set<String> basePackages = resolveBasePackages(metadata);

        return basePackages.stream()
                .map(provider::findCandidateComponents)
                .flatMap(Collection::stream)
                .filter(AnnotatedBeanDefinition.class::isInstance)
                .map(AnnotatedBeanDefinition.class::cast);
    }

    protected ClassPathScanningCandidateComponentProvider createPackageComponentProvider() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return metadata.isIndependent() && !metadata.isAnnotation();
            }
        };
        provider.addIncludeFilter(includeFilter());
        provider.addExcludeFilter(excludeFilter());
        provider.setResourceLoader(getResourceLoader());
        return provider;
    }

}