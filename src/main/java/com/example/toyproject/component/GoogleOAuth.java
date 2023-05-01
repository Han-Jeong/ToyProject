package com.example.toyproject.component;

import com.example.toyproject.dto.oauth.GoogleOAuthTokenDto;
import com.example.toyproject.dto.oauth.GoogleUserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class GoogleOAuth {

    @Value("${google.login.url}")
    private String googleLoginURL;
    @Value("${google.auth.token.url}")
    private String GOOGLE_TOKEN_REQUEST_URL;
    @Value("${google.redirect.uri}")
    private String googleRedirectURL;
    @Value("${google.client.id}")
    private String googleClientId;
    @Value("${google.secret}")
    private String googleClientSecret;
    @Value("${google.userinfo.request.url}")
    private String GOOGLE_USERINFO_REQUEST_URL;
    @Value("${google.auth.scope}")
    private String scopes;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public String getScopeURL() {
        return scopes.replaceAll(",", "%20");
    }

    //init google login method used in controller
    public String googleInitURL() {

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", getGoogleClientId());
        params.put("redirect_uri", getGoogleRedirectURL());
        params.put("response_type", "code");
        params.put("scope", getScopeURL());

        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return getGoogleLoginURL()
                +"/o/oauth2/v2/auth"
                +"?"
                +paramStr;
    }
    //method(used in OAuthService)
    //아래 메서드들을 통해 최종적으로 GoogleUserInfoDto에 정보를 담도록 한다.

    /**
     * 일회용 코드를 다시 구글로 보내 액세스 토큰을 포함한 JSON String이 담긴 ResponseEntity 받기
     * @param code
     * @return null or responseEntity
     * null -> !HttpStatus.OK
     */
    public ResponseEntity<String> requestAccessToken(String code) {

        log.info("111");
        //RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectURL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL,
                params, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        }
        return null;
    }

    /**
     * token 정보를 담은 responseEntity를 통해 token정보를 담은 dto 생성
     * @param response
     * @return googleOAuthTokenDto
     * @throws JsonProcessingException
     */
    public GoogleOAuthTokenDto getAccessToken(ResponseEntity<String> response)throws JsonProcessingException {
        log.info("222");
        log.info("response.getBody()={}",response.getBody());
        //ObjectMapper objectMapper = new ObjectMapper();
        GoogleOAuthTokenDto googleOAuthTokenDto = objectMapper.readValue(response.getBody(), GoogleOAuthTokenDto.class);
        return googleOAuthTokenDto;
    }

    /**
     * token정보를 담은 dto를 통해 사용자 정보를 요청한 결과인 responseEntity 리턴
     * @param oAuthTokenDto
     * @return responseEntity
     */
    public ResponseEntity<String> requestUserInfo(GoogleOAuthTokenDto oAuthTokenDto) {

        log.info("333");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthTokenDto.getAccess_token());

        log.info("headers={}",headers);
        //RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(GOOGLE_USERINFO_REQUEST_URL, HttpMethod.GET, request, String.class);
        log.info("response.getBody()={}",response.getBody());

        return response;
    }
    
    public GoogleUserInfoDto getUserInfo(ResponseEntity<String> response)throws JsonProcessingException {

        log.info("444");
        //ObjectMapper objectMapper = new ObjectMapper();
        GoogleUserInfoDto googleUserInfoDto = objectMapper.readValue(response.getBody(), GoogleUserInfoDto.class);
        return googleUserInfoDto;
    }

}
