package com.example.ktb.repository;

import com.example.ktb.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글 기준으로 삭제되지 않은 댓글 수 세기
    long countByPost_PostIdAndIsDeletedFalse(Long postId);

    // 댓글 리스트 가져오기
    Optional<List<Comment>> findAllByPost_PostId(Long postId);
}


