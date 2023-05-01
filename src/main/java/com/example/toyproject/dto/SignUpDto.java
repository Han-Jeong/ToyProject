package com.example.toyproject.dto;

import com.example.toyproject.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignUpDto {

    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호를 8자 이상 15자 이하로 입력해주세요.")
    private String password;
    @NotBlank
    private String name;

    public Member toEntity() {
        Member member = Member.builder()
                .email(email)
                .password(password)
                .name(name).build();
        return member;
    }
    //
    @Builder
    public SignUpDto(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

}
