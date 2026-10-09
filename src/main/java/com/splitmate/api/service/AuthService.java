package com.splitmate.api.service;

import com.splitmate.api.dto.request.LoginRequest;
import com.splitmate.api.dto.response.LoginResponse;
import com.splitmate.api.entity.User;
import com.splitmate.api.exception.AuthenticationException;
import com.splitmate.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final String INVALID_CREDENTIALS_MSG = "Email/phone or password is incorrect.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String identifier = request.emailOrPhone().trim();

        User user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> {
                    log.debug("Login attempt with unknown identifier: {}", maskIdentifier(identifier));
                    return new AuthenticationException(INVALID_CREDENTIALS_MSG);
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.debug("Login attempt with incorrect password for user: {}", user.getId());
            throw new AuthenticationException(INVALID_CREDENTIALS_MSG);
        }

        if (!user.isActive()) {
            log.debug("Login attempt for inactive account: {}", user.getId());
            throw new AuthenticationException(INVALID_CREDENTIALS_MSG);
        }

        String accessToken = jwtService.generateToken(
                user.getId(), user.getEmail(), user.getDisplayName()
        );

        log.info("User {} logged in successfully.", user.getId());

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                new LoginResponse.UserInfo(
                        user.getId().toString(),
                        user.getDisplayName(),
                        user.getEmail()
                )
        );
    }

    private String maskIdentifier(String identifier) {
        if (identifier == null || identifier.length() < 4) {
            return "***";
        }
        return identifier.substring(0, 2) + "***" + identifier.substring(identifier.length() - 2);
    }
}
