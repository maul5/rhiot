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
package io.rhiot.cloudlets

import com.example.MockHttpExchangeInterceptor
import io.rhiot.cloudlets.device.DeviceCloudlet
import io.rhiot.mongodb.EmbeddedMongo
import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.bootstrap.classpath.ClasspathBeans.APPLICATION_PACKAGE_PROPERTY
import static io.rhiot.utils.Properties.setStringProperty

class CustomHttpExchangeInterceptorTest extends Assert {

    @BeforeClass
    static void beforeClass() {
        new EmbeddedMongo().start()
        setStringProperty(APPLICATION_PACKAGE_PROPERTY, 'com.example')
        new DeviceCloudlet().start().waitFor()
    }

    @Test
    void shouldCallInterceptor() {
        IOUtils.toString(new URI("http://localhost:15000/device"))
        assertThat(MockHttpExchangeInterceptor.hasBeenCalled).isTrue()
    }

}
