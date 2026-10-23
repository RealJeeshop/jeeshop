package org.rembx.jeeshop.configuration;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Injection test using weld container
 */
public class ConfigurationProducerCT {

    protected static Weld weld;
    protected static WeldContainer container;

    @BeforeAll
    public static void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterAll
    public static void close() {
        weld.shutdown();
    }

    @Test
    public void withConfiguredFileProperty_shouldBeInjectedAsConfiguredValue() throws Exception {
        NamedConfigurationUse injected = container.select(NamedConfigurationUse.class).get();
        assertNotNull(injected);
        assertEquals("dummyHost", injected.getHostName());
        assertEquals("dummyTimeout", injected.getTimeout());
    }

    @Test
    public void withNotConfiguredFileProperty_shouldBeInjectedAsNull() throws Exception {
        NamedConfigurationUse injected = container.select(NamedConfigurationUse.class).get();
        assertNotNull(injected);
        assertNull(injected.getPort());
    }

    @Test
    public void withUnknownFile_shouldThrowException() throws Exception {

        assertThrows(IllegalStateException.class,
                () -> container.select(NamedConfigurationWithoutConfigurationFile.class).get());
    }

}

class NamedConfigurationUse {

    @Inject
    @NamedConfiguration("host.name")
    private String hostName;

    @Inject
    @NamedConfiguration("toto.toto")
    private String port;

    @Inject
    @NamedConfiguration(value = "timeout", configurationFile = "/namedconfigurationcustom.properties")
    private String timeout;

    String getTimeout() {
        return timeout;
    }

    String getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

}

class NamedConfigurationWithoutConfigurationFile {

    @Inject
    @NamedConfiguration("toto.unknown")
    private String hostName;

    public String getHostName() {
        return hostName;
    }

}
