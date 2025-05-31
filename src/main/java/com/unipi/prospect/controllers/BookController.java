package com.unipi.prospect.controllers;

import com.unipi.prospect.product.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    // Endpoint to search for books using Google Books API
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(defaultValue = "bestsellers") String query) {
        try {
            // Google Books API URL with query parameters
            String url = GOOGLE_BOOKS_API_URL + "?q=" + query + "&langRestrict=en&gl=us&filter=ebooks&maxResults=5";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("items")) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
            List<Book> books = new ArrayList<>();

            for (Map<String, Object> item : items) {
                books.add(parseBookFromResponse(item));
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
        try {
            String url = GOOGLE_BOOKS_API_URL + "/" + id;
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            
            Book book = parseBookFromResponse(response);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    // Helper method to parse book details from the API response
    private Book parseBookFromResponse(Map<String, Object> item) {
        String id = (String) item.get("id");
        Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

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
        
        return new Book(id, title, authorString, imageUrl, description, publisher,
                        publishedDate, pageCount, genreString, previewLink);
    }
}
