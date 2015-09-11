/**
 * Licensed to the Camel Labs under one or more
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
package io.rhiot.mongodb

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net

import static de.flapdoodle.embed.mongo.distribution.Version.V3_1_0
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setIntProperty

class EmbeddedMongo {

    final def int port = findAvailableTcpPort()

    EmbeddedMongo start() {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V3_1_0)
                .net(new Net(port, localhostIsIPv6()))
                .build()
        MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

        setIntProperty('MONGODB_SERVICE_PORT', port)

        this
    }

    int port() {
        port
    }

}