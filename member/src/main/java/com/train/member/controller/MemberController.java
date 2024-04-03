package com.train.member.controller;

import com.train.member.request.MemberRegisterReq;
import com.train.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("register")
    public Long register(@Valid @RequestBody MemberRegisterReq req) {
        return memberService.register(req.getMobile());
    }
}
