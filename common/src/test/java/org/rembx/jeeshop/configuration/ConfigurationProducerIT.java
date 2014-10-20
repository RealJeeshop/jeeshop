package org.rembx.jeeshop.configuration;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 * Injection test using weld container
 */
public class ConfigurationProducerIT {

    protected static Weld weld;
    protected static WeldContainer container;

    @BeforeClass
    public static void init() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterClass
    public static void close() {
        weld.shutdown();
    }

    @Test
    public void withConfiguredFileProperty_shouldBeInjectedAsConfiguredValue() throws Exception {
        NamedConfigurationUse injected = container.instance().select(NamedConfigurationUse.class).get();
        assertNotNull(injected);
        assertEquals("dummyHost", injected.getHostName());
        assertEquals("dummyTimeout", injected.getTimeout());
    }

    @Test
    public void withNotConfiguredFileProperty_shouldBeInjectedAsNull() throws Exception {
        NamedConfigurationUse injected = container.instance().select(NamedConfigurationUse.class).get();
        assertNotNull(injected);
        assertNull(injected.getPort());
    }

    @Test
    public void withUnknownFile_shouldThrowException() throws Exception {

        try{
            NamedConfigurationWithoutConfigurationFile injected = container.instance().select(NamedConfigurationWithoutConfigurationFile.class).get();
            fail();
        }catch (IllegalStateException e){

        }
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
