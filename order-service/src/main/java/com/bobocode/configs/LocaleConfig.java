package com.bobocode.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

/**
 * Configuration to enforce English locale across the application.
 */
@Configuration
public class LocaleConfig {

    /**
     * Configures a LocaleResolver that always returns English locale,
     * overriding any client-side Accept-Language headers.
     *
     * @return the LocaleResolver bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(Locale.ENGLISH);
    }
}
