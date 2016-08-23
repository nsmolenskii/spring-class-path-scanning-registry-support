# Spring class path scanning support

## Goal
The main idea is to provide an ability to create spring boot starters for dynamic client libraries 
such as:
 * [Retrofit](https://github.com/square/retrofit)
 * [CXF clients](https://github.com/apache/cxf)
 * ...

## Example of library usage

* Create a marker annotation
```java
@Inherited
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetrofitClient {
    String value();
}
```

* Create a factory 

```java
public class HttpClientFactoryBean<T> extends TypedFactoryBeanSupport<T> {
    @Override
    protected T createObject(Class<T> objectType) {
        return getApplicationContext().getBean(RetrofitFactory.class).getInstance(objectType);
    }
}
```

* Create a registrar

```java
public class RetrofitClientsRegistrar extends ClassPathScanningRegistrarSupport {
    @Override
    protected Filter includeFilter() {
        return Filters.annotated(HttpClient.class);
    }

    @Override
    protected Filter excludeFilter() {
        return Filters.nothing();
    }

    @Override
    protected void registerBeanDefinition(AnnotatedBeanDefinition definition, BeanDefinitionRegistry registry) {
        BeanDefinitionRegistrars.withTypedFactory(HttpClientFactoryBean.class, definition, registry);
    }
}
```

* Create an import meta annotations to enable scanning
```java
@Inherited
@ComponentScan
@Configuration
@Target(TYPE)
@Retention(RUNTIME)
@Import(RetrofitClientsRegistrar.class)
public @interface EnableRetrofitClients {
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] basePackages() default {};
}
```

## Example of client usage
* Define Spring Bean for `RetrofitFactory`
* Apply configuration in client on SpringBootApplication

```java
@SpringBootApplication
@EnableRetrofitClients(basePackages = "optional.package.to.scan")
public static class Application {
}
```

* Define interface for Retrofit client

```java
@RetrofitClient("github")
public interface GitHubClient {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
```

* Use created client within services 

```java
@Service
public class GitHubRepositoryService {
    @Autowired
    private GitHubClient gitHubClient;
}
```

* Profit

