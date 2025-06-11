package com.unipi.prospect.controllers;

import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.users.Author;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
@RequestMapping("/books")
public class BookSearchController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";
    BookDao bookDao = new BookDao();
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false, defaultValue = "relevance") String sortBy,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false, defaultValue = "10") Integer maxResults,
            @RequestParam(required = false, defaultValue = "0") Integer startIndex,
            Model model) {

        if (query == null && author == null && genre == null && isbn == null) {
            // No search criteria provided, render empty search page
            return "searchresults";
        }
        try {
            // Build search query string
            StringBuilder searchQueryBuilder = new StringBuilder();
            if (isbn != null && !isbn.isEmpty()) {
                searchQueryBuilder.append("isbn:").append(isbn.trim());
            }else {
                if (query != null && !query.isEmpty()) {
                    searchQueryBuilder.append(query.trim());
                }

                if (author != null && !author.isEmpty()) {
                    if (searchQueryBuilder.length() > 0) searchQueryBuilder.append("+");
                    searchQueryBuilder.append("inauthor:").append(author.trim());
                }

                if (genre != null && !genre.isEmpty()) {
                    if (searchQueryBuilder.length() > 0) searchQueryBuilder.append("+");
                    searchQueryBuilder.append("subject:").append(genre.trim());
                }

                if (isbn != null && !isbn.isEmpty()) {
                    if (searchQueryBuilder.length() > 0) searchQueryBuilder.append("+");
                    searchQueryBuilder.append("isbn:").append(isbn.trim());
                }
            }
                // Build URL with query parameters
                StringBuilder urlBuilder = new StringBuilder(GOOGLE_BOOKS_API_URL);
                urlBuilder.append("?q=").append(searchQueryBuilder.toString())
                        .append("&startIndex=").append(startIndex)
                        .append("&maxResults=").append(maxResults)
                        .append("&orderBy=").append(sortBy);

                // Add optional filters
                if (language != null && !language.isEmpty()) {
                    urlBuilder.append("&langRestrict=").append(language);
                }

                // Use printType for type filter
                if (type != null && !type.isEmpty()) {
                    urlBuilder.append("&filter=").append(type);
                }

                // Use filter for availability
                if (availability != null && !availability.isEmpty()) {
                    urlBuilder.append("&filter=").append(availability);
                }
            // Fetch data from Google Books API
            Map<String, Object> response = restTemplate.getForObject(urlBuilder.toString(), Map.class);

            List<Book> books = new ArrayList<>();
            int totalItems = 0;

            if (response != null && response.containsKey("items")) {
                totalItems = (int) response.get("totalItems");
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

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
            }

            // Pass data to the view
            model.addAttribute("books", books);
            model.addAttribute("totalResults", totalItems);
            model.addAttribute("startIndex", startIndex);
            model.addAttribute("maxResults", maxResults);
            model.addAttribute("hasMore", startIndex + maxResults < totalItems);

            // Pass search parameters back to the view for pagination
            model.addAttribute("query", query);
            model.addAttribute("author", author);
            model.addAttribute("genre", genre);
            model.addAttribute("language", language);
            model.addAttribute("type", type);
            model.addAttribute("availability", availability);
            model.addAttribute("priceMin", priceMin);
            model.addAttribute("priceMax", priceMax);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("isbn", isbn);


            return "searchresults";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching books: " + e.getMessage());
            return "searchresults";
        }
    }

    // Helper method to parse book details from the API response
    private Book parseBookFromResponse(Map<String, Object> item) {
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

        // Extract title and author(s)
        String title = (String) volumeInfo.get("title");
        List<String> authors = volumeInfo.containsKey("authors") ?
            (List<String>) volumeInfo.get("authors") : Collections.singletonList("Unknown Author");
        String authorString = String.join(", ", authors);

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

        List<String> categories = volumeInfo.containsKey("categories") ?
            (List<String>) volumeInfo.get("categories") : Collections.emptyList();
        String genreString = String.join(", ", categories);

        String previewLink = volumeInfo.containsKey("previewLink") ?
            (String) volumeInfo.get("previewLink") : "";

        // Price extraction
        Map<String, Object> saleInfo = (Map<String, Object>) item.get("saleInfo");
        float price = 0.0f; // Default price if not available

        if (saleInfo != null && "FOR_SALE".equals(saleInfo.get("saleability"))) {
            Map<String, Object> retailPrice = (Map<String, Object>) saleInfo.get("retailPrice");
            if (retailPrice != null) {
                // Handle different number types
                Object amount = retailPrice.get("amount");
                if (amount instanceof Integer) {
                    price = ((Integer) amount).floatValue();
                } else if (amount instanceof Double) {
                    price = ((Double) amount).floatValue();
                } else if (amount instanceof Float) {
                    price = (Float) amount;
                }
            }
        }

        return new Book(isbn, title, authorString, imageUrl, description, publisher,
                        publishedDate, pageCount, genreString, previewLink, price, 10);
    }
}
