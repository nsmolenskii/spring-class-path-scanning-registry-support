package de.yoki.spring.support.filter;

import org.springframework.core.type.filter.TypeFilter;

/**
 * Extend original {@link TypeFilter} to provide chain operations
 *
 * @see TypeFilter
 */
public interface Filter extends TypeFilter {

    default Filter and(TypeFilter that) {
        return (reader, factory) -> this.match(reader, factory) && that.match(reader, factory);
    }

    default Filter or(TypeFilter that) {
        return (reader, factory) -> this.match(reader, factory) || that.match(reader, factory);
    }
}
