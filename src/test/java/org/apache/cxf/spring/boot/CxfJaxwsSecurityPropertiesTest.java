package org.apache.cxf.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CxfJaxwsSecurityProperties}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class CxfJaxwsSecurityPropertiesTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        CxfJaxwsSecurityProperties properties = new CxfJaxwsSecurityProperties();
        assertThat(properties.isEnabled()).isFalse();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CxfJaxwsSecurityProperties properties = new CxfJaxwsSecurityProperties();
        properties.setEnabled(true);
        assertThat(properties.isEnabled()).isTrue();
    }

    @Test
    void prefix_shouldBeCorrect() {
        assertThat(CxfJaxwsSecurityProperties.PREFIX).isEqualTo("cxf.jaxws.saml");
    }
}
