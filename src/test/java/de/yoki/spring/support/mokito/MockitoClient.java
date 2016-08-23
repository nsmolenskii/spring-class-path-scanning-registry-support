package de.yoki.spring.support.mokito;

import org.springframework.stereotype.Component;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Component
@Target(TYPE)
@Retention(RUNTIME)
public @interface MockitoClient {
}
