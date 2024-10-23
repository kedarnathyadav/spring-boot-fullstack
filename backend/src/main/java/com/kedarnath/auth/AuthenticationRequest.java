package com.kedarnath.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
