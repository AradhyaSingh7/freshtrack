package com.freshtrack.freshtrack.auth;
import com.freshtrack.freshtrack.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String name;
    private Role role;
}