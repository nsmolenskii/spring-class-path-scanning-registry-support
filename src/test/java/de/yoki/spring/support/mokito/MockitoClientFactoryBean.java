package de.yoki.spring.support.mokito;

import de.yoki.spring.support.factory.TypedFactoryBeanSupport;
import org.mockito.Mockito;

public class MockitoClientFactoryBean extends TypedFactoryBeanSupport<Object> {
    @Override
    protected Object createObject(Class<Object> objectType) {
        return Mockito.mock(objectType);
    }
}