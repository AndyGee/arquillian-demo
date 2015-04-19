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

import com.tomitribe.application.BookService;
import com.tomitribe.application.IBookService;
import com.tomitribe.entities.Book;
import com.tomitribe.presentation.BookBean;
import org.apache.openejb.client.RemoteInitialContextFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.InitialContext;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Run the test using the Arquillian test provider
 */
@RunWith(Arquillian.class)
public class ArquillianCube {

    /**
     * This test will deploy the war file to the container.
     * It will NOT contain this actual test class, which is tested
     * as a client.
     *
     * @return WebArchive to deploy
     */
    @Deployment(testable = false)
    public static WebArchive deploy() {

        //Name of war file is just convenient
        return ShrinkWrap.create(WebArchive.class, ArquillianCube.class.getName() + ".war")

                //Add classes required to test
                .addClasses(
                        IBookService.class,
                        BookService.class,
                        BookBean.class
                )

                        //Add packages required to test
                .addPackage("com.tomitribe.entities")

                        //Use our project test-persistence.xml file
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")

                        //Turns on CDI in EE6
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URL serverUrl;

    private static final String TITLE = "Of Mice and Men";

    @Test
    @InSequence(1)
    @RunAsClient
    public void testBookServiceAddBook() throws Exception {
        Book b = new Book();
        b.setBookTitle(TITLE);

        b = getBookService(serverUrl).addBook(b);

        Assert.assertTrue("Book ID was not set", b.getBookId() > 0);
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void testBookServiceGetBooks() throws Exception {

        final List<Book> allBooks = getBookService(serverUrl).getAllBooks();
        boolean found = false;

        for (final Book b : allBooks) {
            if (TITLE.equals(b.getBookTitle())) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("Failed to find the book", found);
    }

    /**
     * Get the remote EJB interface using the provided server URI
     *
     * @param serverUrl Server URI
     * @return IBookService
     * @throws Exception On error
     */
    private static IBookService getBookService(final URL serverUrl) throws Exception {

        String url = serverUrl.toExternalForm();
        url = url.substring(0, url.indexOf('/', url.lastIndexOf(':'))) + "/tomee/ejb";

        final Properties env = new Properties();
        env.put("java.naming.factory.initial", RemoteInitialContextFactory.class.getName());
        env.put("java.naming.provider.url", url);


        final InitialContext ctx = new InitialContext(env);

        final IBookService service = (IBookService) ctx.lookup("BookServiceRemote");
        return service;
    }
}
