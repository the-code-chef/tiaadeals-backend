package com.tiaadeals.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for authentication responses
 * 
 * @author TiaaDeals Team
 * @version 1.0.0
 */
public class AuthResponseDto {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("tokenType")
    private String tokenType = "Bearer";

    @JsonProperty("expiresIn")
    private Long expiresIn;

    @JsonProperty("user")
    private UserDto user;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("issuedAt")
    private LocalDateTime issuedAt;

    // Constructors
    public AuthResponseDto() {}

    public AuthResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.issuedAt = LocalDateTime.now();
    }

    public AuthResponseDto(Boolean success, String message, String token, UserDto user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
        this.issuedAt = LocalDateTime.now();
    }

    public AuthResponseDto(Boolean success, String message, String token, String refreshToken, Long expiresIn, UserDto user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.issuedAt = LocalDateTime.now();
    }

    // Static factory methods
    public static AuthResponseDto success(String message, String token, UserDto user) {
        return new AuthResponseDto(true, message, token, user);
    }

    public static AuthResponseDto success(String message, String token, String refreshToken, Long expiresIn, UserDto user) {
        return new AuthResponseDto(true, message, token, refreshToken, expiresIn, user);
    }

    public static AuthResponseDto failure(String message) {
        return new AuthResponseDto(false, message);
    }

    // Getters and Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    @Override
    public String toString() {
        return "AuthResponseDto{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + (token != null ? "***" : null) + '\'' +
                ", refreshToken='" + (refreshToken != null ? "***" : null) + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                ", issuedAt=" + issuedAt +
                '}';
    }
} 