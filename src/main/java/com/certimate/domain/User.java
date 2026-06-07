package com.certimate.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER")
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "pw", nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    private String major;
    private String interest;
    private String status;

    @Column(name = "kakao_id", unique = true)
    private String kakaoId;

    @Column(name = "agree_consent", nullable = false)
    private Boolean agreeConsent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}