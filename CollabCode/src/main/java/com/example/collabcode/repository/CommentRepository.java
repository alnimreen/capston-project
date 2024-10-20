package com.example.collabcode.repository;

import com.example.collabcode.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByFileId(String fileId);

    List<Comment> findByRoomId(String fileId);

    List<Comment> findByRoomIdAndFileId(String roomId, String fileId);
}