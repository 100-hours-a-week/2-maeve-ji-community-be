package com.example.ktb.service;

import com.example.ktb.dto.CommentDto;
import com.example.ktb.entity.Comment;
import com.example.ktb.entity.Post;
import com.example.ktb.entity.User;
import com.example.ktb.repository.CommentRepository;
import com.example.ktb.repository.PostRepository;
import com.example.ktb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    // 댓글 등록
    @Transactional
    public Long createComment(CommentDto commentDto, Long postId, Long userId) {
        // 받아온 댓글 내용 찍기
        log.info(">> Service: 입력 받은 댓글 내용: {}", commentDto.getComment());
        log.info(">> Service: 사용자 Id = {}", commentDto.getUserId());
        log.info(">> Service: 게시글 Id = {}", commentDto.getPostId());

        // 필수 파라미터 검증
        if (commentDto.getComment() == null || commentDto.getComment().isBlank()) {
            throw new IllegalArgumentException("댓글 내용이 비어있습니다.");
        }

        // 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));

        post.increaseCommentCount();
        log.info("댓글 개수 증가 = {}", post.getPostComment());
        // 댓글 생성
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .comment(commentDto.getComment())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 저장
        Comment savedComment = commentRepository.save(comment);

        // 저장 후 ID 반환 (절대 null이면 안됨)
        if (savedComment.getCommentId() == null) {
            throw new IllegalStateException("댓글 저장 실패");
        }

        return savedComment.getCommentId();
    }
}
