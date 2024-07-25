package com.task.library_book_management_system.service;

import com.task.library_book_management_system.entity.Book;
import com.task.library_book_management_system.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBooks() {
        Book book1 = new Book(1L, "Title1", "Author1", "ISBN1", null);
        Book book2 = new Book(2L, "Title2", "Author2", "ISBN2", null);
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        assertEquals(2, bookService.getAllBooks().size());
    }

    @Test
    public void testAddBook() {
        Book book = new Book(1L, "Title1", "Author1", "ISBN1", null);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        assertEquals(book, bookService.addBook(book));
    }

    @Test
    public void testGetBookById() {
        Book book = new Book(1L, "Title1", "Author1", "ISBN1", null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertTrue(bookService.getBookById(1L).isPresent());
        assertEquals(book, bookService.getBookById(1L).get());
    }

    @Test
    public void testUpdateBook() {
        Book existingBook = new Book(1L, "Old Title", "Old Author", "Old ISBN", null);
        Book updatedBook = new Book(1L, "New Title", "New Author", "New ISBN", null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        assertEquals(updatedBook, bookService.updateBook(1L, updatedBook));
    }

    @Test
    public void testUpdateBookNotFound() {
        Book updatedBook = new Book(1L, "New Title", "New Author", "New ISBN", null);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> bookService.updateBook(1L, updatedBook));
        assertEquals("Book not found with id 1", thrown.getMessage());
    }

    @Test
    public void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }
}