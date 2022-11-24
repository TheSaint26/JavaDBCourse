package com.example.spring_intro.service;

import com.example.spring_intro.model.entity.Book;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);
    List<String> findAllAuthorsWithBooksWithReleaseDateBefore(int year);
    List<String> findAllBooksByAuthorName(String firstName, String lastName);
}
