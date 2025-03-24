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
import java.util.*;

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
//                .modifiedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 저장
        Comment savedComment = commentRepository.save(comment);

        // 저장 후 ID 반환
        if (savedComment.getCommentId() == null) {
            throw new IllegalStateException("댓글 저장 실패");
        }

        return savedComment.getCommentId();
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long postId, Long commentId, Long userId, String content) {
        log.info(">> Service: postId={}", postId);
        log.info(">> Service: userId={}", userId);
        log.info(">> Service: commentId={}", commentId);
        log.info(">> Service: content={}", content);

        if (content == null) {
            throw new IllegalArgumentException("댓글 수정:: comment 누락");
        }

        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment_not_found"));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다");
        }

        comment.setComment(content);
        comment.setModifiedAt(LocalDateTime.now());
    }

    // 댓글 삭제 (soft deleted)
    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment_not_found"));

        if (!postId.equals(comment.getPost().getPostId())) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        comment.setIsDeleted(true);
        comment.setModifiedAt(LocalDateTime.now());
    }

    // 댓글 목록 조회
    // 댓글 목록 조회
    @Transactional
    public List<Map<String, Object>> getCommentsByPost(Long postId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

        // 댓글 조회
        List<Comment> comments = commentRepository.findByPostPostIdAndIsDeletedFalse(postId);
        List<Map<String, Object>> commentList = new ArrayList<>();

        for (Comment comment : comments) {
            Map<String, Object> author = Map.of(
                    "user_id", comment.getUser().getUserId(),
                    "nickname", comment.getUser().getNickname(),
                    "img_url", comment.getUser().getImgUrl()
            );

            Map<String, Object> commentData = new LinkedHashMap<>();
            commentData.put("id", comment.getCommentId());
            commentData.put("comment", comment.getComment());
            commentData.put("author", author);
            commentData.put("created_at", comment.getCreatedAt());
            commentData.put("modified_at", comment.getModifiedAt());
            commentData.put("is_deleted", comment.getIsDeleted());

            commentList.add(commentData);
        }

        return commentList;
    }



}
