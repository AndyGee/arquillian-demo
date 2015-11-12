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
package com.jax;

import com.jax.application.BookService;
import com.jax.application.BookServiceImpl;
import com.jax.entities.Book;
import com.jax.presentation.BookBean;
import com.jax.service.HelloWorldService;
import com.jax.servlet.HelloWorldServlet;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.openejb.loader.IO;
import org.apache.openejb.testing.EnableServices;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WebAppVersionType;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Application;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

@RunWith(Arquillian.class)
@EnableServices({"jax-ws"})
public class ArquillianHeavyTest {

    @Deployment(testable = true)
    public static WebArchive deploy() {

        /**
         * This looks nasty at first glance, but what we are doing here
         * is just creating the common web.xml file.
         */

        final String xml = Descriptors.create(WebAppDescriptor.class)
                .version(WebAppVersionType._3_0)
                .getOrCreateServlet()
                .servletName("jaxrs")
                .servletClass(Application.class.getName())
                .createInitParam()
                .paramName(Application.class.getName())
                .paramValue(Application.class.getName())
                .up()
                .up()
                .getOrCreateServletMapping()
                .servletName("jaxrs")
                .urlPattern("/api")
                .up()
                .exportAsString();

        final Collection<String> dependencies = Collections.singletonList(
                "org.apache.openjpa:openjpa:2.3.0"
        );

        final File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve(dependencies)
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, ArquillianHeavyTest.class.getName()
                + ".war") //Name is just convenient

                .addClasses(
                        BookService.class,
                        BookServiceImpl.class,
                        MockBookService.class,
                        Book.class,
                        BookBean.class,
                        HelloWorldService.class,
                        HelloWorldServlet.class
                )

                .addAsLibraries(libs)

                .addAsResource("persistence.xml", "META-INF/persistence.xml")

                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")

                .addAsWebInfResource(EmptyAsset.INSTANCE, "ejb-jar.xml") //Turn on scanning

                .setWebXML(new StringAsset(xml));
    }

    /**
     * Injected resource URL
     */
    @ArquillianResource
    public URL url;

    @Test
    @RunAsClient
    public void invokeWebService() throws Exception {

        //final URL url = new URL("http://localhost:" + System.getProperty("server.http.port"));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            final URI uri = URI.create(url.toExternalForm() + "HelloWorldServiceService");
            final HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity("" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <ns1:getMessage xmlns:ns1=\"http://service.jax.com/\"/>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>"));

            final HttpResponse response = httpClient.execute(post);
            final String body = asString(response);

            final String expected = "" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<soap:Body>" +
                    "<ns:getMessageResponse xmlns:ns=\"http://service.jax.com/\">" +
                    "<return>The Green Mile</return>" +
                    "</ns:getMessageResponse>" +
                    "</soap:Body>" +
                    "</soap:Envelope>";

            Assert.assertEquals(expected, body.replaceAll("ns[0-9]*", "ns"));

        }
    }

    public static String asString(final HttpResponse execute) throws IOException {
        try (InputStream in = execute.getEntity().getContent()) {
            return IO.slurp(in);
        }
    }
}
