package com.unipi.prospect.controllers;

import com.unipi.prospect.communication.Comment;
import com.unipi.prospect.db.communication.CommentDao;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.users.Author;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.sql.Date;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
public class BookController {

    // RestTemplate to make HTTP requests to Google Books API
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";
    BookDao bookDao = new BookDao();

    // Endpoint to search for books using Google Books API
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(defaultValue = "bestsellers") String query) {
        System.out.println("SEARCH IS BEING EXECUTED");
        try {
            // Google Books API URL with query parameters
            String url = GOOGLE_BOOKS_API_URL + "?q=" + query + "&langRestrict=en&gl=us&filter=ebooks&maxResults=10";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("items")) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            List<Book> books = new ArrayList<>();

            for (Map<String, Object> item : items) {

                Book book = parseBookFromResponse(item);
            if (book != null) {
                if (new UserDAO().selectByUsername(book.getAuthorUsername(),"Author") != null){
                    new UserDAO().insert(new Author(book.getAuthorUsername(), "000", book.getAuthorUsername(), book.getAuthorUsername(), true));
                }
                bookDao.insert(book);
                books.add(book);
            }
            }

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


    // Endpoint to get a book by its ID using Google Books API
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        System.out.println("ID IS BEING EXECUTED");
        try {
            String url = GOOGLE_BOOKS_API_URL + "/" + id;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null) {
                return ResponseEntity.notFound().build();
            }

            Book book = parseBookFromResponse(response);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchBooksByAuthor(@RequestParam String author) {
        System.out.println("AUTHOR SEARCH IS BEING EXECUTED");
        System.out.println("AUTHOR SEARCH IS BEING EXECUTED");
        System.out.println("AUTHOR SEARCH IS BEING EXECUTED");
        try {
            if (author == null || author.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            String searchQuery = "inauthor:" + author.trim().replace(" ", "+");
            String url = GOOGLE_BOOKS_API_URL + "?q=" + searchQuery + "&langRestrict=en&gl=us&filter=ebooks&maxResults=10";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("items")) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            List<Book> books = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Book book = parseBookFromResponse(item);
                if (book != null)
                {
                    if(bookDao.selectByIsbn(book.getIsbn()) == null)
                    {
                        bookDao.insert(book);

                    }

                    books.add(book);
                }
            }

            return ResponseEntity.ok(books);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.emptyList());
        }
    }



    // Helper method to parse book details from the API response
    private Book parseBookFromResponse(Map<String, Object> item) {
        System.out.println("PARSE IS BEING EXECUTED");
        String id = (String) item.get("id");
        Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");


        // Extract ISBN
        String isbn = "Unknown ISBN";
        if (volumeInfo.containsKey("industryIdentifiers")) {
            List<Map<String, String>> identifiers = (List<Map<String, String>>) volumeInfo.get("industryIdentifiers");
            for (Map<String, String> identifier : identifiers) {
                if ("ISBN_13".equals(identifier.get("type"))) {
                    isbn = identifier.get("identifier");
                    break;
                } else if ("ISBN_10".equals(identifier.get("type")) && "Unknown ISBN".equals(isbn)) {
                    // Fallback to ISBN_10 if ISBN_13 isn't found
                    isbn = identifier.get("identifier");
                }
            }
        }
        if (isbn.equals("Unknown ISBN")) {
            // Skip books without ISBN
            return null;
        }
        System.out.println(isbn);

        // Extract title and author(s)
        String title = (String) volumeInfo.get("title");
        System.out.println(title);
        List<String> authorUsername = volumeInfo.containsKey("authors") ?
            (List<String>) volumeInfo.get("authors") : Collections.singletonList("Unknown Author");
        String authorString = String.join(", ", authorUsername);

        // Default placeholder image
        String imageUrl = "https://img.icons8.com/?size=180&id=fOU7z641V5Z0&format=png&color=000000";
        if (volumeInfo.containsKey("imageLinks")) {
            Map<String, String> imageLinks = (Map<String, String>) volumeInfo.get("imageLinks");
            if (imageLinks.containsKey("thumbnail")) {
                imageUrl = imageLinks.get("thumbnail");
                // Replace http with https for secure image URLs
                imageUrl = imageUrl.replace("http://", "https://");
            }
        }

        // Extract additional book details
        String description = volumeInfo.containsKey("description") ?
            (String) volumeInfo.get("description") : "No description available";

        String publisher = volumeInfo.containsKey("publisher") ?
            (String) volumeInfo.get("publisher") : "Unknown Publisher";

        String publishedDate = volumeInfo.containsKey("publishedDate") ?
            (String) volumeInfo.get("publishedDate") : "Unknown";

        int pageCount = volumeInfo.containsKey("pageCount") ?
            ((Number) volumeInfo.get("pageCount")).intValue() : 0;

        List<String> genre = volumeInfo.containsKey("categories") ?
            (List<String>) volumeInfo.get("categories") : Collections.emptyList();
        String genreString = String.join(", ", genre);

        String previewLink = volumeInfo.containsKey("previewLink") ?
            (String) volumeInfo.get("previewLink") : "";

        // Price extraction
        Map<String, Object> saleInfo = (Map<String, Object>) item.get("saleInfo");
        double price = 0.0; // Default price if not available

        if (saleInfo != null && "FOR_SALE".equals(saleInfo.get("saleability"))) {
            Map<String, Object> retailPrice = (Map<String, Object>) saleInfo.get("retailPrice");
            if (retailPrice != null) {
                // Check if the price is double or integer
                if (retailPrice.get("amount") instanceof Integer) {
                    price = ((Integer) retailPrice.get("amount")).doubleValue();
                } else if (retailPrice.get("amount") instanceof Double) {
                    // If it's already a double, just cast it
                    price = (Double) retailPrice.get("amount");
                } else if (retailPrice.get("amount") instanceof String) {
                    // Fallback to 0.0 if the type is unexpected
                    price = 0.0;
                } else {
                    // Fallback to 0.0 if the type is unexpected
                    price = 0.0;
                }
            }
        }

        return new Book(isbn, title, authorString, imageUrl, description, publisher,
                        publishedDate, pageCount, genreString, previewLink, (float) price, 10);
    }
}
