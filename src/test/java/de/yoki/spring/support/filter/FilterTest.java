package de.yoki.spring.support.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FilterTest {

    @Mock
    private MetadataReader reader;
    @Mock
    private MetadataReaderFactory factory;

    private Filter always = Filters.everything();
    private Filter never = Filters.nothing();

    @Test
    public void shouldCheckAndPositive() throws Exception {
        Filter filter = always.and(always);

        assertThat(filter.match(reader, factory), is(true));

        verifyZeroInteractions(reader, factory);
    }

    @Test
    public void shouldCheckAndNegative() throws Exception {
        Filter filter = always.and(never);

        assertThat(filter.match(reader, factory), is(false));

        verifyZeroInteractions(reader, factory);
    }

    @Test
    public void shouldCheckOrPositive() throws Exception {
        Filter filter = never.or(always);

        assertThat(filter.match(reader, factory), is(true));

        verifyZeroInteractions(reader, factory);
    }

    @Test
    public void shouldCheckOrNegative() throws Exception {
        Filter filter = never.or(never);

        assertThat(filter.match(reader, factory), is(false));

        verifyZeroInteractions(reader, factory);
    }

}