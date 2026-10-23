package org.rembx.jeeshop.configuration

import jakarta.inject.Inject
import org.jboss.weld.environment.se.Weld
import org.jboss.weld.environment.se.WeldContainer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ConfigurationProducerCT {
    @Test
    fun withConfiguredFileProperty_shouldBeInjectedAsConfiguredValue() {
        val injected = container.select(NamedConfigurationUse::class.java).get()
        assertNotNull(injected)
        assertEquals("dummyHost", injected.hostName)
        assertEquals("dummyTimeout", injected.timeout)
    }

    @Test
    fun withNotConfiguredFileProperty_shouldBeInjectedAsNull() {
        val injected = container.select(NamedConfigurationUse::class.java).get()
        assertNotNull(injected)
        assertNull(injected.port)
    }

    @Test
    fun withUnknownFile_shouldThrowException() {
        assertThrows(IllegalStateException::class.java) {
            container.select(NamedConfigurationWithoutConfigurationFile::class.java).get()
        }
    }

    companion object {
        private lateinit var weld: Weld
        private lateinit var container: WeldContainer

        @BeforeAll
        @JvmStatic
        fun init() {
            weld = Weld().disableDiscovery().addBeanClasses(
                ConfigurationProducer::class.java,
                NamedConfigurationUse::class.java,
                NamedConfigurationWithoutConfigurationFile::class.java,
            )
            container = weld.initialize()
        }

        @AfterAll
        @JvmStatic
        fun close() {
            weld.shutdown()
        }
    }
}

class NamedConfigurationUse {
    @Inject
    @field:NamedConfiguration("host.name")
    lateinit var hostName: String

    @Inject
    @field:NamedConfiguration("toto.toto")
    var port: String? = null

    @Inject
    @field:NamedConfiguration(value = "timeout", configurationFile = "/namedconfigurationcustom.properties")
    lateinit var timeout: String
}

class NamedConfigurationWithoutConfigurationFile {
    @Inject
    @field:NamedConfiguration("toto.unknown")
    lateinit var hostName: String
}
