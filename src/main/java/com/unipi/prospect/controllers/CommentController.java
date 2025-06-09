package com.unipi.prospect.controllers;

import com.unipi.prospect.communication.Comment;
import com.unipi.prospect.db.communication.CommentDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private final CommentDao commentDao;

    public CommentController()
    {
        this.commentDao = new CommentDao();
    }

    // 1. Fetch comments for a given book by ISBN
    @GetMapping("/comments")
    public List<Comment> getComments(@RequestParam("isbn") String isbn) {

        return commentDao.selectByBookIsbn(isbn);
    }
    @PostMapping("/comment/save")
    public ResponseEntity<String> saveComment(
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating,
            @RequestParam("isbn") String isbn) {

        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        CommentDao commentDao = new CommentDao();
        Comment userComment = new Comment("Egg", sqlDate, comment, isbn, -1, rating);
        commentDao.insert(userComment);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/comment/reply")
    public ResponseEntity<String> saveReplies(
            @RequestParam("comment") String comment,
            @RequestParam("username") String username,
            @RequestParam("isbn") String isbn,
            @RequestParam("replyTo") List<Integer> replyToIds
    ) {
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        CommentDao commentDao = new CommentDao();

        for (Integer replyToId : replyToIds) {

            Comment reply = new Comment(
                    username,
                    sqlDate,
                    comment,
                    isbn,
                    replyToId,
                    0
            );


            System.out.println(commentDao.insert(reply));
        }

        return ResponseEntity.ok("Success");
    }












}