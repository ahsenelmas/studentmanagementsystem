package com.examples.studentmanagementsystem.dto.response;

import com.examples.studentmanagementsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String username;
    private Role role;
}