package com.example.ktb.service;

import com.example.ktb.dto.PostDto;
import com.example.ktb.entity.Comment;
import com.example.ktb.entity.Post;
import com.example.ktb.entity.User;
import com.example.ktb.repository.CommentRepository;
import com.example.ktb.repository.PostRepository;
import com.example.ktb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 게시물 전체 조회
    @Transactional
    public List<Map<String, Object>> getAllPosts() {
        List<Post> postList = postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();

        List<Map<String, Object>> posts = new ArrayList<>();
        for (Post post : postList) {
            Map<String, Object> author = Map.of(
                    "user_id", post.getUser().getUserId(),
                    "nickname", post.getUser().getNickname(),
                    "img_url", post.getUser().getImgUrl()
            );

            Map<String, Object> postData = new LinkedHashMap<>();
            postData.put("post_id", post.getPostId());
            postData.put("title", post.getTitle());
            postData.put("author", author);
            postData.put("created_at", post.getCreatedAt().toString());
            postData.put("countRecommend", post.getPostLike());
            postData.put("countComment", post.getPostComment());
            postData.put("countView", post.getPostView());

            posts.add(postData);
        }

        return posts;
    }

    // 게시글 등록
    @Transactional
    public Long createPost(PostDto postDto, Long userId) {
        if (postDto.getTitle() == null || postDto.getContent() == null) {
            throw new IllegalArgumentException("필수값 누락");
        }
        String ImgUrl = postDto.getImgUrl();
        System.out.println(">>>> ImgUrl: " + ImgUrl);   // 이미지 url 들어오는지 확인

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));

        Post post = Post.builder()
                .user(user)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .imgUrl(postDto.getImgUrl())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        postRepository.save(post);
        return post.getPostId();
    }

    // 게시물 단일 조회
    @Transactional
    public Map<String, Object> getPostDetail(Long postId) {
        // 조회수 증가
        postRepository.increasePostView(postId);

        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));
        // 글 작성자 정보
        Map<String, Object> author = Map.of(
                "user_id", post.getUser().getUserId(),
                "nickname", post.getUser().getNickname(),
                "img_url", post.getUser().getImgUrl()
        );

        // 댓글 조회 및 구성
        List<Comment> comments = commentRepository.findByPostPostIdAndIsDeletedFalse(postId);
        List<Map<String, Object>> commentList = new ArrayList<>();

        for (Comment comment : comments) {
            Map<String, Object> commentAuthor = Map.of(
                    "user_id", comment.getUser().getUserId(),
                    "nickname", comment.getUser().getNickname(),
                    "img_url", comment.getUser().getImgUrl()
            );

            Map<String, Object> commentData = new LinkedHashMap<>();
            commentData.put("id", comment.getCommentId());
            commentData.put("comment", comment.getComment());
            commentData.put("author", commentAuthor);
            commentData.put("created_at", comment.getCreatedAt());
            commentData.put("modified_at", comment.getModifiedAt());
            commentData.put("is_deleted", comment.getIsDeleted());

            commentList.add(commentData);
        }

        // 응답 데이터 구성
        Map<String, Object> postData = new LinkedHashMap<>();
        postData.put("post_id", post.getPostId());
        postData.put("title", post.getTitle());
        postData.put("contents", post.getContent());
        postData.put("author", author);
        postData.put("countRecommend", post.getPostLike());
        postData.put("countComment", post.getPostComment());
        postData.put("countView", post.getPostView()+1);
        postData.put("img_url", post.getImgUrl());
        postData.put("created_at", post.getCreatedAt());
        postData.put("modified_at", post.getModifiedAt());
        postData.put("is_deleted", post.getIsDeleted());

        // 지금은 comments 생략
        postData.put("comments", commentList);

        return postData;
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, Long userId, String title, String content, String imgUrl) {
        if (title == null || content == null) {
            throw new IllegalArgumentException("필수값 누락");
        }

        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

        // 작성자만 수정 가능
        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        if (imgUrl != null) post.setImgUrl(imgUrl);
        post.setModifiedAt(LocalDateTime.now());
    }

    // 게시글 삭제 (soft delete)
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        post.setIsDeleted(true);
        post.setModifiedAt(LocalDateTime.now());    // 수정되었다고 해야하는지... (삭제된 시간이니까 해두면 좋지않을가)
    }

    @Transactional
    public void likePost(Long postId, Long userId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));
        if (post.getIsDeleted()) {
            log.warn("삭제된 게시글입니다. postId: {}", postId);
            throw new IllegalArgumentException("삭제된 게시글입니다.");
        }
        postRepository.increasePostLike(postId);
        log.info("✅ postId {} 좋아요 증가 완료", postId);

        // 좋아요 수 확인용 출력
        Post updatedPost = postRepository.findById(postId).orElseThrow();
        log.info("현재 postId {} 의 좋아요 수: {}", postId, updatedPost.getPostLike());
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        Post post = postRepository.findByPostIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new IllegalArgumentException("post_not_found"));

        postRepository.decreasePostLike(postId);
        log.info("✅ postId {} 좋아요 감소 완료", postId);

        // 좋아요 수 확인용 출력
        Post updatedPost = postRepository.findById(postId).orElseThrow();
        log.info("현재 postId {} 의 좋아요 수: {}", postId, updatedPost.getPostLike());
    }

}
