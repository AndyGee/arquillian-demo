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
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Arquillian.class)
public class ArquillianLightTest {

    @Deployment
    public static WebArchive deploy() {

        final Collection<String> dependencies = Arrays.asList(
                "javax.servlet:jstl:1.2",
                "taglibs:standard:1.1.2"
        );

        final File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").resolve(dependencies)
                .withTransitivity().asFile();


        final WebArchive war = ShrinkWrap.create(WebArchive.class, ArquillianLightTest.class.getName() + ".war")
                //Name is just convenient

                .addClasses(
                        IBookService.class,
                        BookService.class,
                        Book.class,
                        BookBean.class
                )

                .addAsLibraries(libs)

                .addAsResource("persistence.xml", "META-INF/persistence.xml")

                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        //Merge in our complete webapp
        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory("src/main/webapp").as(GenericArchive.class),
                "/", Filters.includeAll());

        return war; //Turn on CDI
    }

    @Inject
    protected IBookService service;

    final String bookName = "Romeo and Juliet";

    @Test
    @InSequence(1)
    public void testAdd() {

        final Book b = new Book();
        b.setBookTitle(bookName);
        service.addBook(b);
    }

    @Test
    @InSequence(2)
    public void testGetAllBooks() {

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
