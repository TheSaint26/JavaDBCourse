package com.example.spring_intro.service;

import com.example.spring_intro.model.entity.Author;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();

    List<String> getAllAuthorsByCountOfTheirBooksDesc();

}
