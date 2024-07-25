package com.task.library_book_management_system.controller;

import com.task.library_book_management_system.entity.Book;
import com.task.library_book_management_system.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@EnableWebMvc
@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookService bookService;
    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testGetAllBooks() throws Exception {
        Book book1 = new Book(1L, "Title1", "Author1", "123-456-789", LocalDate.of(2024, 1, 1));
        Book book2 = new Book(2L, "Title2", "Author2", "987-654-321", LocalDate.of(2024, 2, 1));
        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'id':1,'title':'Title1','author':'Author1','isbn':'123-456-789','publishedDate':'2024-01-01'},{'id':2,'title':'Title2','author':'Author2','isbn':'987-654-321','publishedDate':'2024-02-01'}]"));
    }

    @Test
    public void testGetBookById() throws Exception {
        Book book = new Book(1L, "Title1", "Author1", "123-456-789", LocalDate.of(2024, 1, 1));
        when(bookService.getBookById(anyLong())).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':1,'title':'Title1','author':'Author1','isbn':'123-456-789','publishedDate':'2024-01-01'}"));
    }

    @Test
    public void testGetBookByIdNotFound() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateBook() throws Exception {
        Book savedBook = new Book(1L, "Title1", "Author1", "123-456-789", LocalDate.of(2024, 1, 1));
        when(bookService.addBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"Title1\",\"author\":\"Author1\",\"isbn\":\"123-456-789\",\"publishedDate\":\"2024-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':1,'title':'Title1','author':'Author1','isbn':'123-456-789','publishedDate':'2024-01-01'}"));
    }

    @Test
    public void testUpdateBook() throws Exception {
        Book book = new Book(1L, "Updated Title", "Updated Author", "987-654-321", LocalDate.of(2024, 2, 1));
        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/books/1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"author\":\"Updated Author\",\"isbn\":\"987-654-321\",\"publishedDate\":\"2024-02-01\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'id':1,'title':'Updated Title','author':'Updated Author','isbn':'987-654-321','publishedDate':'2024-02-01'}"));
    }

    @Test
    public void testUpdateBookNotFound() throws Exception {
        when(bookService.updateBook(anyLong(), any(Book.class))).thenThrow(new RuntimeException("Book not found"));

        mockMvc.perform(put("/books/1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"author\":\"Updated Author\",\"isbn\":\"987-654-321\",\"publishedDate\":\"2024-02-01\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBook() throws Exception {
        Mockito.doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteBookNotFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Book not found")).when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNotFound());
    }
}
