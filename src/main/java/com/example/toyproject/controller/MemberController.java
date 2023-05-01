package com.example.toyproject.controller;

import com.example.toyproject.SessionConst;
import com.example.toyproject.domain.Member;
import com.example.toyproject.dto.MemberInfoDto;
import com.example.toyproject.dto.SignInDto;
import com.example.toyproject.dto.SignUpDto;
import com.example.toyproject.repository.MemberRepository;
import com.example.toyproject.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    //private final MemberRepository memberRepository;

    /**
     * sign-up
     */
    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute("signUpDto", new SignUpDto());
        return "form/member/signUpForm";
    }

    @PostMapping("/sign-up")
    public String signUpMember(@Validated @ModelAttribute("signUpDto") SignUpDto signUpDto,
                               BindingResult bindingResult) {
        // 중복 이메일 검사
        memberService.validateDuplicateMember(signUpDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "form/member/signUpForm";
        }
        memberService.signUp(signUpDto); // 회원가입
        return "redirect:sign-in";
    }

    /**
     * sign-in
     */
    @GetMapping("/sign-in")
    public String signInForm(@ModelAttribute("signInDto") SignInDto signInDto) {
        return "form/member/signInForm";
    }

    @PostMapping("/sign-in")
    public String signIn(@Validated @ModelAttribute("signInDto") SignInDto signInDto,
                         BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "form/member/signInForm";
        }

        // sign-in
        MemberInfoDto memberInfoDto = memberService.signIn(signInDto);

        if (memberInfoDto == null) {
            bindingResult.addError(new FieldError(
                    "signInDto", "email", "존재하지 않는 회원입니다."));
            return "form/member/signInForm";
        }

        // 로그인 성공 세션 처리 (세션에 로그인 회원 정보 보관)
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.SIGN_IN_MEMBER, memberInfoDto);

        return "redirect:/";
    }

    /**
     * sign-out
     */
    @PostMapping("/sign-out")
    public String signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        //if session is null, signOut --> 조건문을 사용할 필요가 없어보여서 지움
        memberService.signOut(session);
        return "redirect:/";
    }


//    @GetMapping("/kakao")
//    public String kakaoCallBack(@RequestParam String code, HttpServletRequest request) {  // throws BaseException
//        System.out.println("code = " + code);   // 인가코드 출력
//        String access_Token = memberService.getKaKaoAccessToken(code);
//        SignInDto signInDto = memberService.createKakaoUser(access_Token);
//
//        // sign-in
//        MemberInfoDto memberInfoDto = memberService.signIn(signInDto);
//
//        // 로그인 성공 세션 처리 (세션에 로그인 회원 정보 보관)
//        HttpSession session = request.getSession();
//        session.setAttribute(SessionConst.SIGN_IN_MEMBER, memberInfoDto);
//        return "redirect:/";
//    }

}
