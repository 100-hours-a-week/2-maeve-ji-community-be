package com.example.ktb.repository;

import com.example.ktb.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // 삭제되지 않은 post 가져오기
    Optional<Post> findByPostIdAndIsDeletedFalse(Long postId);

    /**
     * 게시글 조회수 증가 (UPDATE 쿼리를 직접 호출)
     */
    @Modifying
    @Query("UPDATE Post p SET p.postView = p.postView + 1 WHERE p.postId = :postId")
    void increasePostView(@Param("postId") Long postId);

    /**
     * 게시글 좋아요 수 증가
     */
    @Modifying
    @Query("UPDATE Post p SET p.postLike = p.postLike + 1 WHERE p.postId = :postId")
    void increasePostLike(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.postLike = CASE WHEN p.postLike > 0 THEN p.postLike - 1 ELSE 0 END WHERE p.postId = :postId")
    void decreasePostLike(@Param("postId") Long postId);

    @Query("SELECT p.postLike FROM Post p WHERE p.postId = :postId")
    int getPostLike(@Param("postId") Long postId);

}
