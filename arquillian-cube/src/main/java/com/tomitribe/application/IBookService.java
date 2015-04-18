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
package com.tomitribe.application;

import com.tomitribe.entities.Book;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface IBookService {

    Book addBook(Book book);

    List<Book> getAllBooks();
}
