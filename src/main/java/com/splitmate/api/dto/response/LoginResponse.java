package com.splitmate.api.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserInfo user
) {

    public record UserInfo(
            String id,
            String displayName,
            String email
    ) {
    }
}
