package com.example.toyproject.config;

import com.example.toyproject.SignInCheckInterceptor;
import com.example.toyproject.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private BoardRepository boardRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SignInCheckInterceptor(boardRepository))
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/member/sign-up", "/member/sign-in", "/member/sign-out", "/kakao", "/member/kakao",
                        "/css/**", "/*.ico", "/error", "/images/**", "/google","/google/login/callback");
    }
}
