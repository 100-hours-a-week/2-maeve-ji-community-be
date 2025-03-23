package com.example.ktb.repository;

import com.example.ktb.entity.Like;
import com.example.ktb.entity.Post;
import com.example.ktb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
}
