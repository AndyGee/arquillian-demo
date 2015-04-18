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

import com.tomitribe.application.IBookService;
import com.tomitribe.application.BookService;
import com.tomitribe.entities.Book;
import com.tomitribe.presentation.BookBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

@RunWith(Arquillian.class)
public class ArquillianCube {

    /**
     * This class will deploy the war file to the container.
     * It will actually contain this test class, which is tested
     * within the container context.
     *
     * @return WebArchive to deploy
     */
    @Deployment
    public static WebArchive deploy() {

        //Name of war file is just convenient
        return ShrinkWrap.create(WebArchive.class, ArquillianCube.class.getName() + ".war")

                //Add classes required to test
                .addClasses(
                        BookService.class,
                        BookBean.class,
                        Book.class
                )

                        //Add packages required to test
                //.addPackage("com.tomitribe.entities")

                        //Use our project test-persistence.xml file
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")

                        //Turns on CDI in EE6
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * Because this class is deployed on the container we can inject the EJB
     */
    @EJB
    private IBookService ejb;

    private static final String TITLE = "Of Mice and Men";

    @Test
    @InSequence(1)
    public void testBookServiceAddBook() {
        Book b = new Book();
        b.setBookTitle(TITLE);

        b = ejb.addBook(b);

        Assert.assertTrue("Book ID was not set", b.getBookId() > 0);
    }

    @Test
    @InSequence(2)
    public void testBookServiceGetBooks() {

        final List<Book> allBooks = ejb.getAllBooks();
        boolean found = false;

        for (final Book b : allBooks) {
            if (TITLE.equals(b.getBookTitle())) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("Failed to find the book", found);
    }
}
