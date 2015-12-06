/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.camel.rest

import io.rhiot.bootstrap.classpath.Named
import io.rhiot.datastream.engine.AbstractServiceRouteStreamConsumer
import io.rhiot.bootstrap.classpath.Bean
import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.steroids.camel.Route
import io.vertx.core.json.Json
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class CamelRestServiceStreamSourceTest extends DataStreamTest {

    @Test
    void shouldInvokeGetOperation() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/test/count/1'), Map.class)
        assertThat(response.payload).isEqualTo(1)
    }

    @Test
    void shouldDetectTooShortUri_none() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080'), Map.class)
        assertThat(response.error).isEqualTo(CamelRestStreamSource.URI_TOO_SHORT_MESSAGE)
    }

    @Test
    void shouldDetectTooShortUri_rootOnly() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/'), Map.class)
        assertThat(response.error).isEqualTo(CamelRestStreamSource.URI_TOO_SHORT_MESSAGE)
    }

    @Test
    void shouldDetectTooShortUri_serviceOnly() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/service'), Map.class)
        assertThat(response.error).isEqualTo(CamelRestStreamSource.URI_TOO_SHORT_MESSAGE)
    }

    @Test
    void shouldDetectTooShortUri_serviceWithEmptyOperation() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/service/'), Map.class)
        assertThat(response.error).isEqualTo(CamelRestStreamSource.URI_TOO_SHORT_MESSAGE)
    }

    // Beans fixtures

    static interface TestService {

        int count(int number)

    }

    @Bean
    @Named(name = 'test')
    static class TestInterfaceImpl implements TestService {

        @Override
        int count(int number) {
            number
        }

    }

    @Route
    static class TestInterfaceStreamConsumer extends AbstractServiceRouteStreamConsumer {

        TestInterfaceStreamConsumer() {
            super('test')
        }
    }

}