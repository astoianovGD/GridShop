package com.bobocode.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class LocaleConfigTest {

    @Autowired
    private LocaleResolver localeResolver;

    @Test
    void shouldResolveToEnglishLocale() {
        assertNotNull(localeResolver);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", "uk-UA");

        Locale resolvedLocale = localeResolver.resolveLocale(request);
        assertEquals(Locale.ENGLISH, resolvedLocale);
    }
}
