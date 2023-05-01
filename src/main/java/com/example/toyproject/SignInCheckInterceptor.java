package com.example.toyproject;

import com.example.toyproject.repository.BoardRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class SignInCheckInterceptor implements HandlerInterceptor {
    private BoardRepository boardRepository;

    public SignInCheckInterceptor(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);
        // 로그인하지 않은 사용자 제한
        if (session == null || session.getAttribute(SessionConst.SIGN_IN_MEMBER) == null) {
            log.info("미인증 사용자 요청");
            response.sendRedirect("/member/sign-in?redirectURL=" + requestURI);
            return false;
        }
        return true;
    }
}

