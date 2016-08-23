package de.yoki.spring.support.filter;

import lombok.NoArgsConstructor;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Filters {

    /**
     * @return filter that allow everything
     */
    public static Filter everything() {
        return (reader, factory) -> true;
    }

    /**
     * @return filter that allows nothing
     */
    public static Filter nothing() {
        return (reader, factory) -> false;
    }

    /**
     * @return filter that allows only classes that annotated by target
     */
    public static Filter annotated(Class<? extends Annotation> target) {
        return new AnnotationTypeFilter(target)::match;
    }

    /**
     * @return filter that allows only subclasses of target
     */
    public static Filter assignable(Class<?> target) {
        return new AssignableTypeFilter(target)::match;
    }

    /**
     * @return filter that checks that class all delegate is allowed by all delegates.
     */
    public static Filter and(Filter filter, Filter... filters) {
        return Stream.of(filters).reduce(filter, Filter::and);
    }

    /**
     * @return filter that checks that class all delegate is allowed by all delegates.
     */
    public static Filter or(Filter filter, Filter... filters) {
        return Stream.of(filters).reduce(filter, Filter::or);
    }

}
