package org.rembx.jeeshop.configuration

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.enterprise.inject.spi.InjectionPoint
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.InputStream
import java.util.Properties

@ApplicationScoped
class ConfigurationProducer {
    @Produces
    @NamedConfiguration
    fun retrieveNamedConfiguration(injectionPoint: InjectionPoint): String? {
        val namedConfiguration = injectionPoint.annotated.getAnnotation(NamedConfiguration::class.java)
        if (StringUtils.isEmpty(namedConfiguration.value)) return null

        val configurationFilePath = namedConfiguration.configurationFile.ifEmpty {
            "/${injectionPoint.member.declaringClass.simpleName.lowercase()}$CONFIGURATION_FILE_SUFFIX"
        }
        return loadConfigurationFile(configurationFilePath).getProperty(namedConfiguration.value)
    }

    private fun loadConfigurationFile(configurationFilePath: String): Properties {
        configurations[configurationFilePath]?.let { return it }

        logger.debug("Loading property file : {}", configurationFilePath)
        val properties = Properties()
        try {
            val configurationFile: InputStream = javaClass.getResourceAsStream(configurationFilePath)
                ?: throw IllegalStateException("File :$configurationFilePath not found")
            configurationFile.use { properties.load(it) }
        } catch (e: IOException) {
            throw RuntimeException("Failed to load configuration file: $configurationFilePath", e)
        }

        configurations[configurationFilePath] = properties
        logger.debug("Property file load successful")
        return properties
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ConfigurationProducer::class.java)
        private const val CONFIGURATION_FILE_SUFFIX = ".properties"
        private val configurations = HashMap<String, Properties>()
    }
}
