package com.train.member.controller;

import com.train.common.response.CommonResp;
import com.train.member.request.MemberRegisterReq;
import com.train.member.request.MemberSendCodeReq;
import com.train.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("register")
    public CommonResp<Long> register(@Valid @RequestBody MemberRegisterReq req) {
        long id = memberService.register(req.getMobile());

        return new CommonResp<>(id);
    }

    @PostMapping("/send-code")
    public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
        memberService.sendCode(req);
        return new CommonResp<>();
    }
}
