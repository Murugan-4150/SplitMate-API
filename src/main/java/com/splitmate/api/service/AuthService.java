package com.splitmate.api.service;

import com.splitmate.api.dto.request.LoginRequest;
import com.splitmate.api.dto.request.RegisterRequest;
import com.splitmate.api.dto.response.LoginResponse;
import com.splitmate.api.dto.response.RegisterResponse;
import com.splitmate.api.entity.User;
import com.splitmate.api.exception.AuthenticationException;
import com.splitmate.api.exception.DuplicateResourceException;
import com.splitmate.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final String INVALID_CREDENTIALS_MSG = "Email/phone or password is incorrect.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PhoneNumberService phoneNumberService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       PhoneNumberService phoneNumberService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.phoneNumberService = phoneNumberService;
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

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String normalizedPhone = phoneNumberService.normalizeToE164(request.phoneNumber().trim());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("email",
                    "This email address is already registered. Please login instead.");
        }

        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new DuplicateResourceException("phoneNumber",
                    "This phone number is already registered. Please login instead.");
        }

        User user = new User();
        user.setDisplayName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

        log.info("New user registered: {} ({})", savedUser.getId(), maskIdentifier(normalizedEmail));

        return new RegisterResponse(
                "Registration successful.",
                new RegisterResponse.UserInfo(
                        savedUser.getId().toString(),
                        savedUser.getDisplayName(),
                        savedUser.getEmail(),
                        savedUser.getPhone(),
                        "ACTIVE"
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
