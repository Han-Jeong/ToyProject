package com.example.toyproject.controller.Member;

import com.example.toyproject.component.GoogleOAuth;
import com.example.toyproject.SessionConst;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.service.OAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Slf4j
@Controller
public class GoogleOAuthController {

    @Qualifier("Google")            //oAuthService interface를 상속받는 service중 google 선택
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private GoogleOAuth googleOAuth;

    @GetMapping("/google")
    public void getGoogleAuthURL(HttpServletResponse response) throws IOException {
        log.info("initURL={}",googleOAuth.googleInitURL());
        response.sendRedirect(googleOAuth.googleInitURL());
    }

    @GetMapping("/google/login/callback")
    public String callBack(@RequestParam(name="code")String code, HttpServletRequest request) throws IOException {
        MemberInfoDto memberInfoByGoogle = oAuthService.signInByOAuth(code);
        // 로그인 성공 세션 처리 (세션에 로그인 회원 정보 보관)
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.SIGN_IN_MEMBER, memberInfoByGoogle);
        return "redirect:/";
    }
}
