package de.yoki.spring.support.registrar;

import de.yoki.spring.support.mokito.EnableMockitoClients;
import de.yoki.spring.support.mokito.MockitoClient;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static de.yoki.spring.support.registrar.ClassPathScanningRegistrarSupportIT.Application;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mockingDetails;

@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ClassPathScanningRegistrarSupportIT {

    @Autowired
    private Client client;

    @Test
    public void shouldCreateMockClient() throws Exception {
        assertThat(mockingDetails(client).isMock(), Matchers.is(true));
    }

    @EnableMockitoClients
    @SpringBootApplication
    public static class Application {
    }

    @MockitoClient
    public interface Client {
    }
}