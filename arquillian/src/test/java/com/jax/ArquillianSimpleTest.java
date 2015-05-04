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
import com.jax.application.IBookService;
import com.jax.entities.Book;
import com.jax.presentation.BookBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

@RunWith(Arquillian.class)
public class ArquillianSimpleTest {

    @Deployment
    public static WebArchive deploy() {

        return ShrinkWrap.create(WebArchive.class
                , ArquillianSimpleTest.class.getName() + ".war")
                .addClasses(
                        IBookService.class,
                        BookService.class,
                        Book.class,
                        BookBean.class
                )
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    protected IBookService service;

    @Test
    public void test() {

        final Book b = new Book();
        final String bookName = "Romeo and Juliet";
        b.setBookTitle(bookName);
        service.addBook(b);

        final List<Book> allBooks = service.getAllBooks();
        boolean found = false;

        for (final Book book : allBooks) {
            if (bookName.equals(book.getBookTitle())) {
                found = true;
                break;
            }
        }

        Assert.assertTrue(found);
    }
}
