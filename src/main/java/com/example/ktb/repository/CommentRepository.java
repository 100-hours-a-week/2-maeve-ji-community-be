package com.example.ktb.repository;

import com.example.ktb.entity.Comment;
import com.example.ktb.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글 기준으로 삭제되지 않은 댓글 수 세기
    long countByPost_PostIdAndIsDeletedFalse(Long postId);

    // 게시글에 맞는 댓글 가져오기 (삭제되지 않은)
    List<Comment> findByPostPostIdAndIsDeletedFalse(Long postId);

    // 삭제되지 않은 댓글 가져오기
    Optional<Comment> findByCommentIdAndIsDeletedFalse(Long commentId);
}


