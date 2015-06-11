/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.gateway;

import com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.github.camellabs.iot.utils.Properties.booleanProperty;
import static com.github.camellabs.iot.utils.Properties.intProperty;
import static com.google.common.io.Files.createTempDir;
import static java.lang.System.setProperty;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = GpsCloudletSyncTest.class)
@IntegrationTest("camellabs_iot_gateway_gps_cloudlet_sync=true")
public class GpsCloudletSyncTest extends Assert {

    static File gpsCoordinatesStore = createTempDir();

    static int geofencingApiPort = findAvailableTcpPort();

    static String dbName;

    @BeforeClass
    public static void beforeClass() {
        // Gateway GPS store fixtures
        setProperty("camellabs_iot_gateway_gps_store_directory", gpsCoordinatesStore.getAbsolutePath());
        setProperty("camellabs_iot_gateway_gps_cloudlet_url", "localhost:" + geofencingApiPort);

        // Geofencing cloudlet fixtures
        dbName = "test";
        setProperty("camel.labs.iot.cloudlet.document.driver.mongodb.db", dbName);
        booleanProperty("camel.labs.iot.cloudlet.document.driver.mongodb.embedded", true);
        intProperty("camel.labs.iot.cloudlet.rest.port", geofencingApiPort);

        new Thread() {
            @Override
            public void run() {
                GeofencingCloudlet.main("--spring.main.sources=com.github.camellabs.iot.cloudlet.geofencing.GeofencingCloudlet");
            }
        }.start();
    }

    @Test
    public void shouldInterceptHeartbeatEndpoint() throws InterruptedException, IOException {
        Thread.sleep(10000);
        IOUtils.write(System.currentTimeMillis() + ",10,20", new FileOutputStream(new File(gpsCoordinatesStore, "foo")));
        Thread.sleep(10000);
        assertEquals(1, new MongoClient().getDB(dbName).getCollection("GpsCoordinates").count());
        DBObject object = new MongoClient().getDB(dbName).getCollection("GpsCoordinates").findOne();
        assertEquals(10d, (Double) object.get("latitude"), 0.0);
        assertEquals(20d, (Double) object.get("longitude"), 0.0);
    }

}