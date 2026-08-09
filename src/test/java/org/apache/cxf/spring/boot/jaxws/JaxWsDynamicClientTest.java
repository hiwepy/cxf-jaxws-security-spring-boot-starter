/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.cxf.spring.boot.jaxws;

import org.apache.cxf.spring.boot.jaxws.security.ClientLoginInterceptor;
import org.apache.cxf.phase.Phase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JAX-WS dynamic client related classes.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
class JaxWsDynamicClientTest {

    @Test
    void clientLoginInterceptor_shouldBeCreated() {
        ClientLoginInterceptor interceptor = new ClientLoginInterceptor("admin", "pass");
        assertThat(interceptor).isNotNull();
    }

    @Test
    void clientLoginInterceptor_shouldHaveCorrectPhase() {
        ClientLoginInterceptor interceptor = new ClientLoginInterceptor("user", "secret");
        assertThat(interceptor.getPhase()).isEqualTo(Phase.PREPARE_SEND);
    }
}
