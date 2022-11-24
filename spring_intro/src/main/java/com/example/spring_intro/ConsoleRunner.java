package com.example.spring_intro;

import com.example.spring_intro.model.entity.Book;
import com.example.spring_intro.service.AuthorService;
import com.example.spring_intro.service.BookService;
import com.example.spring_intro.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Scanner;

@Component
public class ConsoleRunner implements CommandLineRunner {
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final Scanner scanner;

    public ConsoleRunner(CategoryService categoryService, AuthorService authorService, BookService bookService) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();
        System.out.println("=======================================");
        System.out.println("Welcome to bookstore service!");
        System.out.println("=======================================");
        while (true) {
            System.out.println("Possible choices:");
            System.out.println("1: Get all books after the year 2000 and print only their titles.");
            System.out.println("2: Get all authors with at least one book with release date before 1990. Print their first name and last name.");
            System.out.println("3: Get all authors, ordered by the number of their books (descending). Print their first name, last name and book count.");
            System.out.println("4: Get all books from author George Powell, ordered by their release date (descending), then by book title (ascending). Print the book's title, release date and copies.");
            System.out.println("5: Exit the system.");
            System.out.println("Please choose exercise:");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> printAllBooksAfterYear(2000);
                case 2 -> printAllAuthorsWithBookBefore1990(1990);
                case 3 -> printAllAuthorsWithCountOfTheirBooksDesc();
                case 4 -> printAllBooksByAuthorNameOrderedByReleaseDate("George", "Powell");
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }

    private void printAllBooksByAuthorNameOrderedByReleaseDate(String firstName, String lastName) {
        bookService
                .findAllBooksByAuthorName(firstName, lastName)
                .forEach(System.out::println);
    }

    private void printAllAuthorsWithCountOfTheirBooksDesc() {
        authorService
                .getAllAuthorsByCountOfTheirBooksDesc()
                .forEach(System.out::println);
    }

    private void printAllAuthorsWithBookBefore1990(int year) {
        bookService
                .findAllAuthorsWithBooksWithReleaseDateBefore(year)
                .forEach(System.out::println);
    }

    private void printAllBooksAfterYear(int year) {
        bookService
                .findAllBooksAfterYear(year)
                .stream()
                .map(Book::getTitle)
                .forEach(System.out::println);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
