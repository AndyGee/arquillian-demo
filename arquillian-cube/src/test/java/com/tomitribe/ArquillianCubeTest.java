/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.tomitribe;

import com.tomitribe.cube.HelloWorldServlet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Run the test using Arquillian and the Arquillian Cube test extension
 * defined by the arquillian.xml configuration file
 */
@RunWith(Arquillian.class)
public class ArquillianCubeTest {

    /**
     * Programmatically enable Java Util Logging configuration.
     * This is just so that we can see verbose console logging.
     */
    static {
        final InputStream inputStream = ArquillianCubeTest.class.getResourceAsStream("/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }

    /**
     * Use ShrinkWrap to build a simple deployment archive.
     * This archive is actually sent to the remote TomEE running
     * on the docker hosted image.
     *
     * @return WebArchive
     */
    @Deployment(testable = false)
    public static WebArchive create() {
        return ShrinkWrap.create(WebArchive.class, "hello.war").addClass(HelloWorldServlet.class);
    }

    /**
     * Provides the test runtime with the remote endpoint host.
     * This is a really convenient way of injecting dynamic information.
     */
    @ArquillianResource
    private URL resource;

    /**
     * Run a completely scope independent client test (@RunAsClient) that
     * communicates with the remote container (TomEE)
     *
     * @throws IOException On error
     */
    @Test
    //@RunAsClient
    public void test() throws IOException {

        final URL obj = new URL(resource, "HelloWorld");
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        String inputLine;
        final StringBuilder response = new StringBuilder();
        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Assert.assertTrue("Hello World".equals(response.toString()));
    }
}