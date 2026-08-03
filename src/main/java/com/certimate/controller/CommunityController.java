package com.certimate.controller;

import com.certimate.domain.CommunityPost;
import com.certimate.domain.CommunityPostLike;
import com.certimate.domain.User;
import com.certimate.repository.CommunityPostRepository;
import com.certimate.repository.CommunityPostLikeRepository;
import com.certimate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostLikeRepository communityPostLikeRepository;
    private final UserRepository userRepository;

    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        
        return userRepository.findByEmail(principal.getName())
                .map(user -> {
                    List<CommunityPost> posts = communityPostRepository.findByNicknameOrderByCreatedAtDesc(user.getId());
                    return ResponseEntity.ok(posts);
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping("/liked-posts")
    public ResponseEntity<?> getLikedPosts(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        return userRepository.findByEmail(principal.getName())
                .map(user -> {
                    // 내가 좋아요 누른 이력 가져오기
                    List<CommunityPostLike> likes = communityPostLikeRepository.findByNickname(user.getId());
                    
                    // 좋아요 누른 게시글 ID 추출
                    List<Long> postIds = likes.stream()
                            .map(CommunityPostLike::getPostId)
                            .collect(Collectors.toList());

                    // ID 리스트로 원본 게시글 가져오기
                    List<CommunityPost> posts = communityPostRepository.findAllById(postIds);
                    
                    return ResponseEntity.ok(posts);
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
