/**
 * Tomitribe Confidential
 * <p/>
 * Copyright(c) Tomitribe Corporation. 2014
 * <p/>
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * <p/>
 */
package com.jax;

import com.jax.application.BookService;
import com.jax.application.IBookService;
import com.jax.entities.Book;

import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Specializes
@Stateless
public class MockBookService extends BookService implements IBookService {

    @Override
    @Path("complex")
    @GET
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    public List<Book> getAllBooks() {
        final Book b = new Book();
        b.setBookId(0);
        b.setBookTitle("The Green Mile");
        return Arrays.asList(b);
    }
}
