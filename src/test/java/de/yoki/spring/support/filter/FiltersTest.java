package de.yoki.spring.support.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FiltersTest {

    @Mock
    private MetadataReader reader;
    @Mock
    private MetadataReaderFactory factory;
    @Mock
    private Filter first;
    @Mock
    private Filter second;
    @Mock
    private Filter third;

    @Test
    public void shouldNotMatchNothing() throws Exception {
        Filter filter = Filters.nothing();

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(false));
        verifyNoMoreInteractions(reader, factory);
    }

    @Test
    public void shouldMatchEverything() throws Exception {
        Filter filter = Filters.everything();

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(true));
        verifyNoMoreInteractions(reader, factory);
    }

    @Test
    public void shouldProvideAnd() throws Exception {
        when(first.and(second)).thenReturn(third);

        Filter actual = Filters.and(first, second);

        assertThat(actual, is(third));
    }

    @Test
    public void shouldProvideOr() throws Exception {
        when(first.or(second)).thenReturn(third);

        Filter actual = Filters.or(first, second);

        assertThat(actual, is(third));
    }

    @Test
    public void shouldMatchAnnotatedPositive() throws Exception {
        setupReaderForClass(reader, Child.class);
        Filter filter = Filters.annotated(Component.class);

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(true));
    }

    @Test
    public void shouldMatchAnnotatedNegative() throws Exception {
        setupReaderForClass(reader, Parent.class);
        Filter filter = Filters.annotated(Component.class);

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(false));
    }

    @Test
    public void shouldMatchAssignablePositive() throws Exception {
        setupReaderForClass(reader, Child.class);
        Filter filter = Filters.assignable(Parent.class);

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(true));
    }

    @Test
    public void shouldMatchAssignableNegative() throws Exception {
        setupReaderForClass(reader, Parent.class);
        Filter filter = Filters.assignable(Child.class);

        boolean actual = filter.match(reader, factory);

        assertThat(actual, is(false));
    }

    private static void setupReaderForClass(MetadataReader reader, Class<?> target) {
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(target);
        AnnotationMetadata metadata = definition.getMetadata();

        when(reader.getAnnotationMetadata()).thenReturn(metadata);
        when(reader.getClassMetadata()).thenReturn(metadata);
    }

    interface Parent {
    }

    @Component
    interface Child extends Parent {
    }

}