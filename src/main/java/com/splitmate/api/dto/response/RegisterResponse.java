package com.splitmate.api.dto.response;

public record RegisterResponse(
        String message,
        UserInfo user
) {

    public record UserInfo(
            String id,
            String fullName,
            String email,
            String phoneNumber,
            String status
    ) {
    }
}
