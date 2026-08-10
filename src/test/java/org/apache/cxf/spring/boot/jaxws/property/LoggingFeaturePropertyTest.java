package org.apache.cxf.spring.boot.jaxws.property;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link LoggingFeatureProperty}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
class LoggingFeaturePropertyTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        LoggingFeatureProperty property = new LoggingFeatureProperty();

        assertThat(property.getLimit()).isEqualTo(49152);
        assertThat(property.getThreshold()).isEqualTo(-1L);
        assertThat(property.isLogBinary()).isFalse();
        assertThat(property.isLogMultipart()).isTrue();
        assertThat(property.isPrettyLogging()).isFalse();
        assertThat(property.isVerbose()).isFalse();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        LoggingFeatureProperty property = new LoggingFeatureProperty();

        property.setLimit(2048);
        assertThat(property.getLimit()).isEqualTo(2048);

        property.setThreshold(100L);
        assertThat(property.getThreshold()).isEqualTo(100L);

        property.setLogBinary(true);
        assertThat(property.isLogBinary()).isTrue();

        property.setLogMultipart(false);
        assertThat(property.isLogMultipart()).isFalse();

        property.setPrettyLogging(true);
        assertThat(property.isPrettyLogging()).isTrue();

        property.setVerbose(true);
        assertThat(property.isVerbose()).isTrue();
    }

}
