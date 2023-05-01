package com.example.toyproject.service;

import com.example.toyproject.domain.Member;


import com.example.toyproject.dto.MemberInfoDto;

import com.example.toyproject.dto.SignInDto;
import com.example.toyproject.dto.SignUpDto;
import com.example.toyproject.repository.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImplV2 implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberInfoDto signIn(SignInDto signInDto) {
        System.out.println("signInDto.email = " + signInDto.getEmail());
        System.out.println("signInDto.password = " + signInDto.getPassword());

        Member test = memberRepository.findByEmail(signInDto.getEmail()).orElse(null);
        if (test == null) {
            System.out.println("is null!!!");
        } else {
            System.out.println("is not null!!!");
        }

        Optional<Member> first = memberRepository.
                findByEmailAndPassword(signInDto.getEmail(), signInDto.getPassword())
                .stream().findFirst();
        if (first.isEmpty()) return null;
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(first.get().getEmail())
                .password(first.get().getPassword())
                .name(first.get().getName())
                .build();

        return memberInfoDto;
    }

    @Transactional
    @Override
    public Long signUp(SignUpDto signUpDto) {
        return memberRepository.save(signUpDto.toEntity()).getId();
    }

    @Override
    public void signOut(HttpSession session) {
        session.invalidate();
    }

    @Override
    public void validateDuplicateMember(SignUpDto signUpDto, BindingResult bindingResult) {
        List<Member> findMember = memberRepository
                .findByEmailAndPassword(signUpDto.getEmail(), signUpDto.getPassword());
        if (!findMember.isEmpty()) {
            bindingResult.addError(new FieldError(
                    "signUpDto", "email", "이미 사용중인 이메일입니다."));
        }
    }



    @Override
    public String getKaKaoAccessToken(String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=33f0972cedce9a91c4222b448fd41a2b"); // REST_API_KEY 입력
            sb.append("&redirect_uri=http://localhost:9090/member/kakao"); // 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return access_Token;
    }

    @Transactional
    @Override
    public SignInDto createKakaoUser(String token){  // throws BaseException

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String ADMIN_TOKEN = "3782db0de7a89f13222fe9c545856ea7";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            System.out.println("element = " + element);
            Long id = element.getAsJsonObject().get("id").getAsLong();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            // TODO null exception

            String nickname = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("profile").getAsJsonObject().get("nickname").getAsString();

            System.out.println("nickname : " + nickname);
            System.out.println("id : " + id);
            System.out.println("email : " + email);


            Member kakaoUser = memberRepository.findByEmail(email).orElse(null);
            SignUpDto kakaoUserDto;
            if (kakaoUser == null) {
                String password = id + ADMIN_TOKEN;
                kakaoUserDto = SignUpDto.builder()
                        .email(email)
                        .password(password)
                        .name(nickname).build();
                kakaoUser = kakaoUserDto.toEntity();
                signUp(kakaoUserDto);
            }
            // return SignInDto
            SignInDto signInDto = SignInDto.builder()
                    .email(kakaoUser.getEmail())
                    .password(kakaoUser.getPassword()).build();

            br.close();
            return signInDto;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
