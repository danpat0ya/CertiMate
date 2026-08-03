package com.certimate.service;

import com.certimate.domain.User;
import com.certimate.dto.AuthDtos.*;
import com.certimate.repository.UserCertificationRepository;
import com.certimate.repository.UserLearnLogRepository;
import com.certimate.repository.UserQuizHistoryRepository;
import com.certimate.repository.UserRepository;
import com.certimate.repository.UserScrapRepository;
import com.certimate.security.JwtProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserCertificationRepository userCertificationRepository;
    private final UserScrapRepository userScrapRepository;
    private final UserLearnLogRepository userLearnLogRepository;
    private final UserQuizHistoryRepository userQuizHistoryRepository;

    // application.yml 에 적어둔 카카오 열쇠(REST API 키)를 가져옵니다.
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // ==========================================
    // [1. 일반 이메일 회원가입]
    // ==========================================
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (request.getAgreeConsent() == null || !request.getAgreeConsent()) {
            throw new IllegalArgumentException("개인정보 처리에 동의해주세요.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .major(request.getMajor())
                .interest(request.getInterest())
                .status(request.getStatus())
                .agreeConsent(request.getAgreeConsent())
                .build();

        userRepository.save(user);
    }

    // ==========================================
    // [2. 일반 이메일 로그인]
    // ==========================================
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtProvider.generateToken(user.getEmail());
    }

    // ==========================================
    // 🚨 [3. 카카오 소셜 로그인 (Null 방어막 적용 완료)]
    // ==========================================
    @Transactional
    public String kakaoLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // STEP 1: 카카오 본사에 인가 코드를 주고 '카카오 액세스 토큰'을 받아옵니다.
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", kakaoClientId);
            body.add("redirect_uri", kakaoRedirectUri);
            body.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
            ResponseEntity<String> tokenResponse = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token", HttpMethod.POST, tokenRequest, String.class);

            JsonNode tokenNode = objectMapper.readTree(tokenResponse.getBody());
            String kakaoAccessToken = tokenNode.get("access_token").asText();

            // STEP 2: 받은 '카카오 액세스 토큰'으로 실제 '카카오 유저 정보'를 요청합니다.
            HttpHeaders infoHeaders = new HttpHeaders();
            infoHeaders.add("Authorization", "Bearer " + kakaoAccessToken);
            infoHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            HttpEntity<MultiValueMap<String, String>> infoRequest = new HttpEntity<>(infoHeaders);
            ResponseEntity<String> infoResponse = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", HttpMethod.POST, infoRequest, String.class);

            JsonNode infoNode = objectMapper.readTree(infoResponse.getBody());

            // ----------------------------------------------------
            // 🛡️ 방어적 파싱 로직 (NullPointerException 완벽 차단)
            // ----------------------------------------------------
            String kakaoId = infoNode.get("id").asText();

            // 이름(닉네임) 기본값 세팅 및 파싱
            String name = "카카오유저_" + kakaoId.substring(0, 4);
            if (infoNode.has("properties") && infoNode.get("properties").has("nickname")
                    && !infoNode.get("properties").get("nickname").isNull()) {
                name = infoNode.get("properties").get("nickname").asText();
            }

            // 이메일 기본값 세팅 및 파싱
            String email = kakaoId + "@kakao.com";
            if (infoNode.has("kakao_account") && infoNode.get("kakao_account").has("email")
                    && !infoNode.get("kakao_account").get("email").isNull()) {
                email = infoNode.get("kakao_account").get("email").asText();
            }

            // STEP 3: 우리 DB에 있는지 확인하고, 없으면 자동 회원가입!
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 카카오 유저는 비번을 몰라도 됨
                        .name(name)
                        .kakaoId(kakaoId)
                        .agreeConsent(true) // 카카오 가입 시 동의한 것으로 간주
                        .major("미입력")
                        .build();
                userRepository.save(user);
            }

            // STEP 4: 로그인 처리 완료. 우리 시스템 전용 JWT 출입증 발급!
            return jwtProvider.generateToken(user.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 로그인 중 서버 오류가 발생했습니다.");
        }
    }

    // ==========================================
    // [4. 회원탈퇴]
    // ==========================================
    @Transactional
    public void withdraw(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        
        Long userId = user.getId();
        userCertificationRepository.deleteByUser_Id(userId);
        userScrapRepository.deleteByUser_Id(userId);
        userLearnLogRepository.deleteByUser_Id(userId);
        userQuizHistoryRepository.deleteByUserId(userId);
        
        userRepository.delete(user);
    }
}