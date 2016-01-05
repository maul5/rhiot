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
package io.rhiot.datastream.consumer.device

import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.datastream.schema.device.Device
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.datastream.schema.device.DeviceConstants.CHANNEL_DEVICE_GET
import static io.rhiot.datastream.schema.device.DeviceConstants.CHANNEL_DEVICE_LIST
import static io.rhiot.datastream.schema.device.DeviceConstants.CHANNEL_DEVICE_REGISTER
import static io.rhiot.utils.Uuids.uuid

class DeviceDataStreamConsumerTest extends DataStreamTest {

    def device = new Device(uuid())

    @Test
    void shouldRegisterDevice() {
        toBusAndWait(CHANNEL_DEVICE_REGISTER, device)
        def devices = fromBus(CHANNEL_DEVICE_LIST, List.class)
        assertThat(devices).isNotEmpty()
    }

    @Test
    void shouldGetDevice() {
        toBusAndWait(CHANNEL_DEVICE_REGISTER, device)
        def device = fromBus("${CHANNEL_DEVICE_GET}.${device.deviceId}", Device.class)
        assertThat(device.deviceId).isEqualTo(device.deviceId)
    }

    @Test
    void shouldNotGetDevice() {
        def device = fromBus("${CHANNEL_DEVICE_GET}.${uuid()}", Device.class)
        assertThat(device).isNull()
    }

}