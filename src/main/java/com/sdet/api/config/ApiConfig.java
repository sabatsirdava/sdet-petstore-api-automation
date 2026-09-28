package com.sdet.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads API configuration from a classpath properties file with safe runtime
 * overrides for local or CI execution.
 */
public final class ApiConfig {

    private static final String CONFIG_FILE = "config/config.properties";
    private static final String BASE_URL_KEY = "baseUrl";

    private ApiConfig() {
    }

    public static String baseUrl() {
        String systemProperty = System.getProperty(BASE_URL_KEY);
        if (isNotBlank(systemProperty)) {
            return normalize(systemProperty);
        }

        String environmentVariable = System.getenv("API_BASE_URL");
        if (isNotBlank(environmentVariable)) {
            return normalize(environmentVariable);
        }

        Properties properties = new Properties();

        try (InputStream input = ApiConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Missing configuration file: " + CONFIG_FILE
                );
            }

            properties.load(input);

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load " + CONFIG_FILE,
                    exception
            );
        }

        String configuredUrl = properties.getProperty(BASE_URL_KEY);

        if (!isNotBlank(configuredUrl)) {
            throw new IllegalStateException(
                    "Required property '" + BASE_URL_KEY + "' is missing or blank"
            );
        }

        return normalize(configuredUrl);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalize(String value) {
        return value.strip().replaceAll("/+$", "");
    }
}