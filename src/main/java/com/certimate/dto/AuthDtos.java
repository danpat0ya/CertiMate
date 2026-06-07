package com.certimate.dto;

import lombok.Getter;
import lombok.Setter;

public class AuthDtos {
    @Getter @Setter
    public static class RegisterRequest {
        private String email;
        private String password;
        private String name;
        private String major;
        private String interest;
        private String status;
        private Boolean agreeConsent;
    }

    @Getter @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }
}