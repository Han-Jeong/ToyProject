package com.example.toyproject.controller;

import com.example.toyproject.SessionConst;
import com.example.toyproject.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
public class HomeController {
    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConst.SIGN_IN_MEMBER, required = false) Member signInMember, Model model) {
        if (signInMember == null) return "redirect:/member/sign-in";
        model.addAttribute("member", signInMember);
        return "redirect:/board/" + signInMember.getMemberId();
    }
}

