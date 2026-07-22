package com.certimate.controller;

import com.certimate.dto.AuthDtos.*;
import com.certimate.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.security.Principal;
import com.certimate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authService.login(request);
        setCookie(response, token);
        return ResponseEntity.ok("로그인에 성공했습니다.");
    }

    // 🚨 [캡스톤 추가] 카카오 로그인 API 창구
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String code = request.get("code"); // 프론트엔드가 보낸 인가 코드

        // 서비스에서 카카오와 통신 후 JWT 토큰을 받아옵니다.
        String token = authService.kakaoLogin(code);

        // 보안 쿠키를 구워줍니다.
        setCookie(response, token);
        return ResponseEntity.ok("카카오 로그인에 성공했습니다.");
    }

    // 중복되는 쿠키 굽기 로직을 메서드로 분리하여 깔끔하게 정리했습니다.
    private void setCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false) // 로컬 테스트용. 나중에 HTTPS 배포 시 true로 변경!
                .path("/")
                .maxAge(60 * 60) // 1시간 유지
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return userRepository.findByEmail(principal.getName())
                .map(user -> ResponseEntity.ok(new UserInfoResponse(user.getName(), user.getMajor(), user.getInterest(), user.getStatus(), user.getProfileImage())))
                .orElse(ResponseEntity.notFound().build());
    }

    public record UserInfoResponse(String name, String major, String interest, String status, String profileImage) {}

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(Principal principal, @RequestBody UpdateProfileRequest request) {
        if (principal == null) return ResponseEntity.status(401).build();
        return userRepository.findByEmail(principal.getName())
                .map(user -> {
                    String encodedPassword = null;
                    if (request.password() != null && !request.password().isBlank()) {
                        encodedPassword = passwordEncoder.encode(request.password());
                    }
                    user.updateProfile(request.name(), request.major(), request.interest(), request.status(), encodedPassword, request.profileImage());
                    userRepository.save(user);
                    return ResponseEntity.ok("프로필이 성공적으로 수정되었습니다.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public record UpdateProfileRequest(String name, String major, String interest, String status, String password, String profileImage) {}
}