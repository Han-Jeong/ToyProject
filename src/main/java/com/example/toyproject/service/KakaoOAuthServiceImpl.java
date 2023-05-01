package com.example.toyproject.service;

import com.example.toyproject.component.KakaoOAuth;
import com.example.toyproject.domain.Member;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.dto.oauth.GoogleOAuthTokenDto;
import com.example.toyproject.dto.oauth.GoogleUserInfoDto;
import com.example.toyproject.dto.oauth.KakaoOAuthTokenDto;
import com.example.toyproject.dto.oauth.KakaoUserInfoDto;
import com.example.toyproject.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service("Kakao")
@Transactional
@RequiredArgsConstructor
public class KakaoOAuthServiceImpl implements OAuthService{

    private final KakaoOAuth kakaoOAuth;
    private final MemberRepository memberRepository;

    @Override
    public MemberInfoDto signInByOAuth(String code) throws IOException {

        KakaoUserInfoDto kakaoUserInfoDto = getKakaoUserInfoDto(code);

        if (memberRepository.findByEmail(kakaoUserInfoDto
                .getKakao_account().getEmail()).orElse(null) == null) {
            memberRepository.save(
                    Member.builder()
                            .name(kakaoUserInfoDto.getProperties().getNickname())
                            .email(kakaoUserInfoDto.getKakao_account().getEmail())
                            .password(kakaoOAuth.getADMIN_TOKEN()).build()
            );
        }
        return getMemberInfoDto(
                kakaoUserInfoDto.getKakao_account().getEmail(),
                kakaoOAuth.getADMIN_TOKEN());
    }

    private MemberInfoDto getMemberInfoDto(String email, String password) {
        Optional<Member> first = memberRepository
                .findByEmailAndPassword(email, password)
                .stream().findFirst();
        log.info("email={}",first.get().getEmail());
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(first.get().getEmail())
                .password(first.get().getPassword())
                .name(first.get().getName())
                .build();
        return memberInfoDto;
    }

    private KakaoUserInfoDto getKakaoUserInfoDto(String code)throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = kakaoOAuth.requestAccessToken(code);
        KakaoOAuthTokenDto oAuthToken = kakaoOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = kakaoOAuth.requestUserInfo(oAuthToken);
        KakaoUserInfoDto kakaoUser = kakaoOAuth.getUserInfo(userInfoResponse);
        return kakaoUser;
    }
}
