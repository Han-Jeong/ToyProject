package com.example.toyproject.component;


import com.example.toyproject.dto.oauth.KakaoOAuthTokenDto;
import com.example.toyproject.dto.oauth.KakaoUserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class KakaoOAuth {

    @Value("${kakao.login.url}")
    private String kakaoLoginURL;
    @Value("${kakao.auth.token.url}")
    private String KAKAO_TOKEN_REQUEST_URL;
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectURL;
    @Value("${kakao.client.id}")
    private String kakaoClientId;
    @Value("${kakao.userinfo.request.url}")
    private String KAKAO_USERINFO_REQUEST_URL;
    @Value("${kakao.admin.token}")
    private String ADMIN_TOKEN;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    //init kakao login
    public String kakaoInitURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", kakaoClientId);
        params.put("redirect_uri", kakaoRedirectURL);
        params.put("response_type", "code");

        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));
        log.info("paramStr={}",paramStr);

        return kakaoLoginURL
                +"?"
                +paramStr;
    }
    //method(used in OAuthService)
    //아래 메서드들을 통해 최종적으로 KakaoUserInfoDto에 정보를 담도록 한다.

    public ResponseEntity<String> requestAccessToken(String code) {

        log.info("111");
        //**google이랑 다른점
        //header에 content-type 설정이 필수
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("code", code);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectURL);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, header);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_TOKEN_REQUEST_URL,
                request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        }
        return null;
    }

    public KakaoOAuthTokenDto getAccessToken(ResponseEntity<String> response)throws JsonProcessingException {
        log.info("222");
        log.info("response.getBody()={}",response.getBody());
        //ObjectMapper objectMapper = new ObjectMapper();
        KakaoOAuthTokenDto kakaoOAuthTokenDto = objectMapper.readValue(response.getBody(), KakaoOAuthTokenDto.class);
        return kakaoOAuthTokenDto;
    }

    public ResponseEntity<String> requestUserInfo(KakaoOAuthTokenDto oAuthTokenDto) {

        log.info("333");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthTokenDto.getAccess_token());

        log.info("headers={}",headers);
        //RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(KAKAO_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
        log.info("response.getBody()={}",response.getBody());

        return response;
    }

    public KakaoUserInfoDto getUserInfo(ResponseEntity<String> response)throws JsonProcessingException {

        log.info("444");
        //ObjectMapper objectMapper = new ObjectMapper();
        KakaoUserInfoDto kakaoUserInfoDto = objectMapper.readValue(response.getBody(), KakaoUserInfoDto.class);
        if(kakaoUserInfoDto.getKakao_account().getHas_email()){
            return kakaoUserInfoDto;
        }
        return null;
    }

}
