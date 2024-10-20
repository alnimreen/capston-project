//package com.example.collabcode.controller;
//
//import com.example.collabcode.model.Comment;
//import com.example.collabcode.service.CommentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@CrossOrigin
//
//@RestController
//@RequestMapping("/api/comments")
//public class CommentController {
//
//    @Autowired
//    private CommentService commentService;
//    @PostMapping("/addComment")
//    public Comment addComment(@RequestBody Comment comment) {
//        return commentService.addComment(comment);
//    }
//
//    @GetMapping("/code/{codeId}/line/{lineNumber}")
//    public List<Comment> getCommentsByLine(@PathVariable String codeId, @PathVariable int lineNumber) {
//        return commentService.getCommentsByLine(codeId, lineNumber);
//    }
//
//}