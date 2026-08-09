package org.apache.cxf.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CxfJaxwsManagerProperties}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class CxfJaxwsManagerPropertiesTest {

    @Test
    void defaultValues_shouldBeCorrect() {
        CxfJaxwsManagerProperties properties = new CxfJaxwsManagerProperties();

        assertThat(properties.isEnabled()).isFalse();
        assertThat(properties.isCreateMBServerConnectorFactory()).isTrue();
        assertThat(properties.isDaemon()).isFalse();
        assertThat(properties.getJmxServiceURL()).isNull();
        assertThat(properties.getServerName()).isNull();
        assertThat(properties.isThreaded()).isFalse();
        assertThat(properties.isUsePlatformMBeanServer()).isFalse();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CxfJaxwsManagerProperties properties = new CxfJaxwsManagerProperties();

        properties.setEnabled(true);
        assertThat(properties.isEnabled()).isTrue();

        properties.setCreateMBServerConnectorFactory(true);
        assertThat(properties.isCreateMBServerConnectorFactory()).isTrue();

        properties.setDaemon(false);
        assertThat(properties.isDaemon()).isFalse();

        properties.setJmxServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");
        assertThat(properties.getJmxServiceURL()).isEqualTo("service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi");

        properties.setServerName("testServer");
        assertThat(properties.getServerName()).isEqualTo("testServer");

        properties.setThreaded(true);
        assertThat(properties.isThreaded()).isTrue();

        properties.setUsePlatformMBeanServer(false);
        assertThat(properties.isUsePlatformMBeanServer()).isFalse();
    }

    @Test
    void prefix_shouldBeCorrect() {
        assertThat(CxfJaxwsManagerProperties.PREFIX).isEqualTo("cxf.manager");
    }

}
