package com.example.toyproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignInDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    @Builder
    public SignInDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

}

