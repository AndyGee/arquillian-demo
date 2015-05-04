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
package com.jax.service;

import com.jax.application.IBookService;
import com.jax.entities.Book;

import javax.inject.Inject;
import javax.jws.WebService;
import java.util.List;

@WebService
public class HelloWorldService {

    @Inject
    private IBookService service;


    public String getMessage() {
        String s = "";
        final List<Book> allBooks = service.getAllBooks();
        for (final Book book : allBooks) {
            s += book.getBookTitle();
        }
        return s;
    }
}
