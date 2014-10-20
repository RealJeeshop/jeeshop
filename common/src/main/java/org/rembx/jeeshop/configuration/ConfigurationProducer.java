package org.rembx.jeeshop.configuration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Configuration Producer
 * <p/>
 * Produces a value for a given @NamedConfiguration annotated property.
 * This value is retrieved from a property file named with the lower case class name where this property is declared
 * <p/>
 * User: remi
 */
public class ConfigurationProducer {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationProducer.class);

    private final static String CONFIGURATION_FILE_SUFFIX = ".properties";

    /**
     * Stores every property files associated to classes using NamedConfiguration annotation
     */
    private static final Map<String, Properties> configurations = new HashMap<>();

    @Produces
    @NamedConfiguration
    String retrieveNamedConfiguration(InjectionPoint injectionPoint) {

        String configurationFilePath;

        NamedConfiguration namedConfiguration = injectionPoint.getAnnotated().getAnnotation(NamedConfiguration.class);
        if (namedConfiguration.value() == null || StringUtils.isEmpty(namedConfiguration.value())) {
            return null;
        }

        if (namedConfiguration.configurationFile() == null || StringUtils.isEmpty(namedConfiguration.configurationFile())) {
            configurationFilePath = "/" + injectionPoint.getMember().getDeclaringClass().getSimpleName().toLowerCase()
                    + CONFIGURATION_FILE_SUFFIX;
        } else {
            configurationFilePath = namedConfiguration.configurationFile();
        }

        Properties properties = loadConfigurationFile(configurationFilePath);

        return properties.getProperty(namedConfiguration.value());

    }

    private Properties loadConfigurationFile(String configurationFilePath) {


        if (configurations.get(configurationFilePath) != null) {
            return configurations.get(configurationFilePath);
        }

        logger.debug("Loading property file : {}", configurationFilePath);

        Properties properties = new Properties();

        try (InputStream configurationFile = getClass().getResourceAsStream(configurationFilePath)) {
            if (configurationFile == null)
                throw new IllegalStateException("File :" + configurationFilePath + " not found");
            properties.load(configurationFile);
            configurations.put(configurationFilePath,properties);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + configurationFilePath, e);
        }

        logger.debug("Property file load successful");
        return properties;
    }


}
