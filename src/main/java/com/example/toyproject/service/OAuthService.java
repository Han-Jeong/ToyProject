package com.example.toyproject.service;

import com.example.toyproject.dto.MemberInfoDto;

import java.io.IOException;

public interface OAuthService {
    MemberInfoDto signInByOAuth(String code) throws IOException;
}
