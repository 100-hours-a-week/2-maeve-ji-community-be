package com.example.ktb.service;

import com.example.ktb.entity.Like;
import com.example.ktb.entity.Post;
import com.example.ktb.entity.User;
import com.example.ktb.repository.LikeRepository;
import com.example.ktb.repository.PostRepository;
import com.example.ktb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  // 좋아요 추가
//  @Transactional
//  public void likePost(Long userId, Long postId) {
//    User user = userRepository.findById(userId)
//        .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
//    Post post = postRepository.findById(postId)
//        .orElseThrow(() -> new IllegalArgumentException("post_not_found"));
//
//    // 이미 눌렀으면
//    likeRepository.findByUserAndPost(user, post).ifPresent(like -> {
//      postRepository.decreasePostLike(postId);
//    });
//
//    // Like 테이블에 저장
//    Like like = Like.builder()
//        .user(user)
//        .post(post)
//        .build();
//    likeRepository.save(like);
//
//    // post_like 증가
//    postRepository.increasePostLike(postId);
//  }

  // 좋아요 토글
  @Transactional
  public boolean toggleLike(Long userId, Long postId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

    // 이미 좋아요한 경우 → 취소
    return likeRepository.findByUserAndPost(user, post).map(existingLike -> {
      likeRepository.delete(existingLike);
      postRepository.decreasePostLike(postId);
      return false; // 좋아요 취소됨
    }).orElseGet(() -> {
      // 좋아요 추가
      Like newLike = Like.builder()
          .user(user)
          .post(post)
          .build();
      likeRepository.save(newLike);
      postRepository.increasePostLike(postId);
      return true; // 좋아요 추가됨
    });
  }

  // 좋아요 취소
  @Transactional
  public void unlikePost(Long userId, Long postId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

    Like like = likeRepository.findByUserAndPost(user, post)
        .orElseThrow(() -> new IllegalArgumentException("like_not_found"));

    // Like 테이블에서 지움
    likeRepository.delete(like);

    // posts 테이블의 post_like 감소
    postRepository.decreasePostLike(postId);
  }
}
