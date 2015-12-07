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
package com.jax.application;

import com.jax.entities.Book;

import java.util.List;

public interface BookService {

    public void addBook(final Book book) ;

    public List<Book> getAllBooks() ;
}
