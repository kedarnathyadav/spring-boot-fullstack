package com.kedarnath.auth;

import com.kedarnath.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO

) {
}
