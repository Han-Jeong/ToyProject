package com.example.toyproject.service;

import com.example.toyproject.component.GoogleOAuth;
import com.example.toyproject.domain.Member;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.dto.oauth.GoogleOAuthTokenDto;
import com.example.toyproject.dto.oauth.GoogleUserInfoDto;
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
@Service("Google")
@Transactional
@RequiredArgsConstructor
public class GoogleOAuthServiceImpl implements OAuthService {

    private final MemberRepository memberRepository;
    private final GoogleOAuth googleOAuth;


    @Override
    public MemberInfoDto signInByOAuth(String code) throws IOException {
        GoogleUserInfoDto googleUser = getGoogleUserInfoDto(code);
        log.info("email={}",googleUser.getEmail());
        log.info("name={}",googleUser.getName());

        //회원정보가 없을 경우 저장
        if (memberRepository.findByEmail(googleUser.getEmail()).orElse(null) == null) {
            //원래는 랜덤한 password를 생성해서 db에 넣어줄 생각이었지만
            //db에서 정보를 가져올 때 email&password를 통해 가져오는 것이 타당하다고 생각해서 password는 해당 플랫폼으로 설정
            //이유는 email만을 가지고 정보를 가져올 때 중복 데이터 발생가능성 우려
            String password = "google";
//            String forPassword = UUID.randomUUID().toString();
//            String password = googleUser.getId()+ forPassword;
            memberRepository.save(
                    Member.builder()
                            .name(googleUser.getName())
                            .email(googleUser.getEmail())
                            .password(password).build()
            );
            //만든 정보를 다시 찾아 dto로 감싸기
            return getMemberInfoDto(googleUser.getEmail(), password);
        }
        //회원정보가 있을 경우 꺼내오기
        return getMemberInfoDto(googleUser.getEmail(), "google");
    }

    //data를 가지고 Member -> MemberInfoDto
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

    //일회성 code -> 해당 회원 정보 dto
    private GoogleUserInfoDto getGoogleUserInfoDto(String code)throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = googleOAuth.requestAccessToken(code);
        GoogleOAuthTokenDto oAuthToken = googleOAuth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = googleOAuth.requestUserInfo(oAuthToken);
        GoogleUserInfoDto googleUser = googleOAuth.getUserInfo(userInfoResponse);
        return googleUser;
    }
}
