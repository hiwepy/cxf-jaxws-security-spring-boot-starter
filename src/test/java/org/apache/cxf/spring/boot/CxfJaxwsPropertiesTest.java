package org.apache.cxf.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CxfJaxwsProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class CxfJaxwsPropertiesTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        CxfJaxwsProperties properties = new CxfJaxwsProperties();
        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.getNamespace()).isNull();
        assertThat(properties.getLoggingFeature()).isNotNull();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CxfJaxwsProperties properties = new CxfJaxwsProperties();
        properties.setEnabled(true);
        assertThat(properties.isEnabled()).isTrue();
        properties.setNamespace("http://example.com");
        assertThat(properties.getNamespace()).isEqualTo("http://example.com");
    }

    @Test
    void loggingFeature_shouldBeSettable() {
        CxfJaxwsProperties properties = new CxfJaxwsProperties();
        assertThat(properties.getLoggingFeature()).isNotNull();
    }
}
