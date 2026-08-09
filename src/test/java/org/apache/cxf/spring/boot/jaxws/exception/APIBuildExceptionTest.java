package org.apache.cxf.spring.boot.jaxws.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link APIBuildException}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class APIBuildExceptionTest {

    @Test
    void constructorWithMessage_shouldStoreMessage() {
        APIBuildException ex = new APIBuildException("build failed");
        assertThat(ex).hasMessage("build failed");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void constructorWithException_shouldWrapCause() {
        Exception cause = new RuntimeException("root cause");
        APIBuildException ex = new APIBuildException(cause);
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    void exception_shouldBeRuntimeException() {
        APIBuildException ex = new APIBuildException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

}
