package org.apache.cxf.spring.boot.jaxws.feature;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EndpointPauseFeature}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class EndpointPauseFeatureTest {

    @Test
    void constructor_shouldCreateInstance() {
        EndpointPauseFeature feature = new EndpointPauseFeature("test-cause");
        assertThat(feature).isNotNull();
    }

    @Test
    void constructorWithNull_shouldCreateInstance() {
        EndpointPauseFeature feature = new EndpointPauseFeature(null);
        assertThat(feature).isNotNull();
    }
}
