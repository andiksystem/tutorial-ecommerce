package com.andik.ecommerce.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class JwtResponse implements Serializable {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private String email;

    public JwtResponse(
            String accessToken,
            String refreshToken,
            String username,
            String email) {
        this.username = username;
        this.email = email;
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }

}
